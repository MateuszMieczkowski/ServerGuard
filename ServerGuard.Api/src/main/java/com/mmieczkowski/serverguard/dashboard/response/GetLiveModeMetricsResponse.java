package com.mmieczkowski.serverguard.dashboard.response;

import com.mmieczkowski.serverguard.metric.model.MetricType;

import java.time.Instant;
import java.util.List;

public record GetLiveModeMetricsResponse(Instant time, List<com.mmieczkowski.serverguard.metric.request.SaveMetricsRequest.SensorMetric> sensorMetrics) {
    public record SensorMetric(String name, List<com.mmieczkowski.serverguard.metric.request.SaveMetricsRequest.SensorMetric.Metric> metrics) {
        public record Metric(String name, Float value, MetricType type) {
        }
    }
}
