package com.mmieczkowski.serverguard.metric.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@ToString
public class Metric {
    private UUID agentId;
    private LocalDateTime time;
    private String sensorName;
    private String metricName;
    private float value;
    private int type;

    public Metric(UUID agentId, LocalDateTime time, String sensorName, String metricName, float value, MetricType type) {
        this.agentId = agentId;
        this.time = time;
        this.sensorName = sensorName;
        this.metricName = metricName;
        this.value = value;
        this.type = type.getValue();
    }
}
