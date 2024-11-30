package com.mmieczkowski.serverguard.metric;

import com.mmieczkowski.serverguard.metric.model.AvailableMetric;
import com.mmieczkowski.serverguard.metric.model.Metric;
import com.mmieczkowski.serverguard.metric.model.MetricType;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Profile("test")
@Repository
public class H2MetricRepository implements MetricRepository {

    @Override
    public void saveAll(List<Metric> metric) {

    }

    @Override
    public List<AvailableMetric> findAvailableMetricsByAgentId(UUID agentId) {
        return List.of();
    }

    @Override
    public List<DataPoint> findLttbMetricsByAgentId(UUID agentId, String sensorName, String metricName, MetricType metricType, LocalDateTime from, LocalDateTime to, int maxDataPoints) {
        return List.of();
    }

    @Override
    public List<DataPoint> findAvgMetricsByAgentId(UUID agentId, String sensorName, String metricName, MetricType metricType, LocalDateTime from, LocalDateTime to, int intervalMinutes) {
        return List.of();
    }

    @Override
    public double findLastMetricValueByAgentId(UUID agentId, String sensorName, String metricName, MetricType metricType, Duration fromNow, String aggregateFunction) {
        return 0;
    }

    @Override
    public void saveAvailableMetrics(UUID agentId, List<AvailableMetric> availableMetrics) {

    }
}
