package com.mmieczkowski.serverguard.alert.response;

import com.mmieczkowski.serverguard.metric.model.MetricType;
import org.springframework.data.domain.Page;

import java.util.UUID;

public record GetAlertsPageResponse(Page<Alert> alerts) {
    public record Alert(
            UUID id,
            String name,
            String sensorName,
            String metricName,
            MetricType metricType,
            float threshold,
            String operator,
            String duration,
            String groupBy
    ) {
    }
}
