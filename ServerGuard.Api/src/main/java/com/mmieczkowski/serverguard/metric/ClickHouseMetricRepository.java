package com.mmieczkowski.serverguard.metric;

import com.clickhouse.client.api.Client;
import com.clickhouse.client.api.data_formats.ClickHouseBinaryFormatReader;
import com.clickhouse.client.api.query.QueryResponse;
import com.clickhouse.client.api.query.QuerySettings;
import com.clickhouse.client.api.query.Records;
import com.mmieczkowski.serverguard.metric.model.AvailableMetric;
import com.mmieczkowski.serverguard.metric.model.Metric;
import com.mmieczkowski.serverguard.metric.model.MetricType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ClickHouseMetricRepository implements MetricRepository {
    private final Client client;

    @PostConstruct
    public void setup() {
        client.ping();
        var metricSchema = client.getTableSchema("metric");
        var availableMetricSchema = client.getTableSchema("available_metric");
        client.register(Metric.class, metricSchema);
        client.register(AvailableMetric.class, availableMetricSchema);
    }

    @Override
    public void saveAll(List<Metric> metrics) {
        client.insert("metric", metrics);
    }

    @Override
    public List<AvailableMetric> findAvailableMetricsByAgentId(UUID agentId) {
        String query = String.format("SELECT distinct m.sensor_name, m.metric_name, m.type FROM metric m WHERE agent_id = '%s'", agentId.toString());
        CompletableFuture<Records> responseCompletableFuture = client.queryRecords(query);
        try(Records queryResponse = responseCompletableFuture.get(3, TimeUnit.SECONDS)) {
            if(queryResponse.getResultRows() == 0) {
                return Collections.emptyList();
            }
            List<AvailableMetric> availableMetrics = new ArrayList<>();
            queryResponse.forEach(reader -> {
                String sensorName = reader.getString("sensor_name");
                String metricName = reader.getString("metric_name");
                int type = reader.getInteger("type");
                var availableMetric = new AvailableMetric(sensorName, metricName, MetricType.values()[type - 1]);
                availableMetrics.add(availableMetric);
            });
            return availableMetrics;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<DataPoint> findLttbMetricsByAgentId(UUID agentId,
                                                    String sensorName,
                                                    String metricName,
                                                    MetricType metricType,
                                                    LocalDateTime from,
                                                    LocalDateTime to,
                                                    int maxDataPoints) {
        String query =
                """
                        SELECT untuple(x)
                        FROM (SELECT lttb({maxDataPoints:UInt64})(time, value) dataPoints
                              FROM (SELECT m.time, m.value
                                    FROM metric m
                                    WHERE agent_id = {agentId:String}
                                      AND m.sensor_name = {sensorName:String}
                                      AND m.metric_name = {metricName:String}
                                      AND m.type = {metricType:Int32}
                                      AND m.time >= {from:DateTime}
                                      AND m.time <= {to:DateTime}
                                    order by time))
                        ARRAY JOIN dataPoints as x;""";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("maxDataPoints", maxDataPoints);
        queryParams.put("agentId", agentId.toString());
        queryParams.put("sensorName", sensorName);
        queryParams.put("metricName", metricName);
        queryParams.put("metricType", metricType.getValue());
        queryParams.put("from", toClickHouseDateTimeFormat(from));
        queryParams.put("to", toClickHouseDateTimeFormat(to));
        CompletableFuture<QueryResponse> responseCompletableFuture = client.query(query, queryParams, new QuerySettings());
        try(QueryResponse queryResponse = responseCompletableFuture.get(3, TimeUnit.SECONDS)) {
            if(queryResponse.getResultRows() == 0) {
                return Collections.emptyList();
            }
            ClickHouseBinaryFormatReader reader = Client.newBinaryFormatReader(queryResponse);
            List<DataPoint> dataPoints = new ArrayList<>();
            while (reader.hasNext()) {
                reader.next();
                DataPoint dataPoint = new DataPoint(reader.getLocalDateTime(1), reader.getDouble(2));
                dataPoints.add(dataPoint);
            }
            return dataPoints;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<DataPoint> findAvgMetricsByAgentId(UUID agentId,
                                                String sensorName,
                                                String metricName,
                                                MetricType metricType,
                                                LocalDateTime from,
                                                LocalDateTime to,
                                                int intervalMinutes) {
        String query =
                """
                        SELECT toStartOfInterval(time, INTERVAL {intervalMinutes:Int32} minute) AS interval,
                               avg(value) AS avg_value
                        FROM metric m
                        WHERE agent_id = {agentId:String}
                          AND m.sensor_name = {sensorName:String}
                          AND m.metric_name = {metricName:String}
                          AND m.type = {metricType:Int32}
                          AND m.time >= {from:DateTime}
                          AND m.time <= {to:DateTime}
                        GROUP BY interval
                                     ORDER BY interval;""";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("intervalMinutes", intervalMinutes);
        queryParams.put("agentId", agentId.toString());
        queryParams.put("sensorName", sensorName);
        queryParams.put("metricName", metricName);
        queryParams.put("metricType", metricType.getValue());
        queryParams.put("from", toClickHouseDateTimeFormat(from));
        queryParams.put("to", toClickHouseDateTimeFormat(to));
        CompletableFuture<QueryResponse> responseCompletableFuture = client.query(query, queryParams, new QuerySettings());
        try(QueryResponse queryResponse = responseCompletableFuture.get(3, TimeUnit.SECONDS)) {
            if(queryResponse.getResultRows() == 0) {
                return Collections.emptyList();
            }
            ClickHouseBinaryFormatReader reader = Client.newBinaryFormatReader(queryResponse);
            List<DataPoint> dataPoints = new ArrayList<>();
            while (reader.hasNext()) {
                reader.next();
                DataPoint dataPoint = new DataPoint(reader.getLocalDateTime(1), reader.getDouble(2));
                dataPoints.add(dataPoint);
            }
            return dataPoints;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public double findLastMetricValueByAgentId(UUID agentId,
                                              String sensorName,
                                              String metricName,
                                              MetricType metricType,
                                              Duration fromNow,
                                              String aggregateFunction) {
        String query = """
                select %s(m.value) from metric m
                where m.agent_id = {agentId:String}
                  AND m.sensor_name =  {sensorName:String}
                  AND m.metric_name = {metricName:String}
                  AND m.type = {metricType:Int32}
                  AND m.time > now() - {secondsFromNow:Int64};""";
        query = String.format(query, aggregateFunction);

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("secondsFromNow", fromNow.getSeconds());
        queryParams.put("agentId", agentId.toString());
        queryParams.put("sensorName", sensorName);
        queryParams.put("metricName", metricName);
        queryParams.put("metricType", metricType.getValue());
        CompletableFuture<QueryResponse> responseCompletableFuture = client.query(query, queryParams, new QuerySettings());
        try(QueryResponse queryResponse = responseCompletableFuture.get(3, TimeUnit.SECONDS)) {
            if(queryResponse.getResultRows() == 0) {
                return 0;
            }
            ClickHouseBinaryFormatReader reader = Client.newBinaryFormatReader(queryResponse);
            reader.next();
            return reader.getDouble(1);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String toClickHouseDateTimeFormat(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

}
