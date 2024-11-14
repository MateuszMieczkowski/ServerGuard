package com.mmieczkowski.serverguard.metric.request;

import com.mmieczkowski.serverguard.metric.model.MetricType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;

public record SaveMetricsRequest(@NotNull Instant time, @Valid List<SensorMetric> sensorMetrics) {
    public record SensorMetric(@NotBlank String name, @Valid List<Metric> metrics) {
        public record Metric(@NotBlank String name, float value, @NotNull MetricType type) {
        }
    }
}
