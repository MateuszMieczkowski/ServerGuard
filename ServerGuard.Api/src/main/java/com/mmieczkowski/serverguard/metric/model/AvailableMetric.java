package com.mmieczkowski.serverguard.metric.model;

import lombok.Getter;

@Getter
public class AvailableMetric {
    private final String sensorName;
    private final String metricName;
    private final int type;

    public AvailableMetric(String sensorName, String metricName, MetricType type) {
        this.sensorName = sensorName;
        this.metricName = metricName;
        this.type = type.getValue();
    }
}
