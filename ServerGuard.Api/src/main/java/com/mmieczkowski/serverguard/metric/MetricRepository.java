package com.mmieczkowski.serverguard.metric;

import com.mmieczkowski.serverguard.metric.model.AvailableMetric;
import com.mmieczkowski.serverguard.metric.model.Metric;
import com.mmieczkowski.serverguard.metric.model.MetricType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface MetricRepository {

    void saveAll(List<Metric> metric);

    List<AvailableMetric> findAvailableMetricsByAgentId(UUID agentId);

    List<DataPoint> findLttbMetricsByAgentId(UUID agentId,
                                             String sensorName,
                                             String metricName,
                                             MetricType metricType,
                                             LocalDateTime from,
                                             LocalDateTime to,
                                             int maxDataPoints);

    List<DataPoint> findAvgMetricsByAgentId(UUID agentId,
                                            String sensorName,
                                            String metricName,
                                            MetricType metricType,
                                            LocalDateTime from,
                                            LocalDateTime to,
                                            int intervalMinutes);


    double findLastMetricValueByAgentId(UUID agentId,
                                       String sensorName,
                                       String metricName,
                                       MetricType metricType,
                                       Duration fromNow,
                                       String aggregateFunction);
}
