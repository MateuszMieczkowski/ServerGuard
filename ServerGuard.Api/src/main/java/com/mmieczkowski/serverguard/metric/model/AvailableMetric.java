package com.mmieczkowski.serverguard.metric.model;

import java.util.UUID;

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

    public UUID getAgentId() {
        return this.agentId;
    }

    public String getSensorName() {
        return this.sensorName;
    }

    public String getMetricName() {
        return this.metricName;
    }

    public int getType() {
        return this.type;
    }
}
