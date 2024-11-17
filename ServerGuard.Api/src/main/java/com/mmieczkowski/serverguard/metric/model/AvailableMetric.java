package com.mmieczkowski.serverguard.metric.model;

import lombok.Getter;

import java.util.UUID;

@Getter
public class AvailableMetric {
    private final UUID agentId;
    private final String sensorName;
    private final String metricName;
    private final int type;

    public AvailableMetric(UUID agentId, String sensorName, String metricName, MetricType type) {
        this.agentId = agentId;
        this.sensorName = sensorName;
        this.metricName = metricName;
        this.type = type.getValue();
    }
}
