package com.mmieczkowski.serverguard.alert.request;

import com.mmieczkowski.serverguard.metric.model.MetricType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.Duration;

public record CreateAlertRequest(
        @Size(min = 3, max = 50) String name,
        @Valid Metric metric,
        @Min(0) @Max(1000) float threshold,
        String operator,
        Duration duration,
        String groupBy
) {
    public record Metric(
            @Size(min = 3, max = 50) String sensorName,
            @Size(min = 3, max = 50) String metricName,
            MetricType type
    ) {
    }
}
