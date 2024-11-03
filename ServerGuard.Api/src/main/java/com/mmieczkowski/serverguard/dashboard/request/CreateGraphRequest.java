package com.mmieczkowski.serverguard.dashboard.request;

import com.mmieczkowski.serverguard.metric.model.MetricType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record CreateGraphRequest(
        @Min(0) int index,
        @Size(min=3, max = 100) String sensorName,
        @Size(min=3, max = 100) String metricName,
        MetricType metricType,
        @Size(min=3, max = 100) String lineColor
) {
}
