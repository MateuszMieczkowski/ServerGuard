package com.mmieczkowski.serverguard.metric.request;

import com.mmieczkowski.serverguard.metric.model.MetricType;

import java.time.Instant;
import java.util.List;

public record SaveMetricsRequest(Instant time, List<SensorMetric> sensorMetrics) {
    public record SensorMetric(String name, List<Metric> metrics) {
        public record Metric(String name, Float value, MetricType type) {
        }
    }
}
