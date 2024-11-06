package com.mmieczkowski.serverguard.alert.response;

import com.mmieczkowski.serverguard.metric.model.MetricType;
import org.springframework.data.domain.Page;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public record GetAlertLogsPageResponse(Page<AlertLog> alertLogs) {
    public record AlertLog(
            UUID id,
            UUID alertId,
            String name,
            String sensorName,
            String metricName,
            MetricType metricType,
            float threshold,
            String operator,
            Duration duration,
            String groupBy,
            Instant triggeredAt,
            float triggeredByValue
    ) {
    }
}
