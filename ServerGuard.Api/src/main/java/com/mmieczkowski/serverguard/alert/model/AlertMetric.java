package com.mmieczkowski.serverguard.alert.model;

import com.mmieczkowski.serverguard.metric.model.MetricType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Enumerated;

@Embeddable
public class AlertMetric {

    @Column(nullable = false)
    private final String sensorName;

    @Column(nullable = false)
    private final String metricName;

    @Column(nullable = false)
    @Enumerated(value = jakarta.persistence.EnumType.STRING)
    private final MetricType type;

    public AlertMetric(String sensorName, String metricName, MetricType type) {
        this.sensorName = sensorName;
        this.metricName = metricName;
        this.type = type;
    }

    public AlertMetric() {
        this.sensorName = null;
        this.metricName = null;
        this.type = null;
    }

    public String getSensorName() {
        return this.sensorName;
    }

    public String getMetricName() {
        return this.metricName;
    }

    public MetricType getType() {
        return this.type;
    }
}
