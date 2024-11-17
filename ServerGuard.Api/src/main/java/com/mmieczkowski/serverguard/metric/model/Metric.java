package com.mmieczkowski.serverguard.metric.model;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

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

    public Metric(UUID agentId, LocalDateTime time, String sensorName, String metricName, float value, int type) {
        this.agentId = agentId;
        this.time = time;
        this.sensorName = sensorName;
        this.metricName = metricName;
        this.value = value;
        this.type = type;
    }

    public UUID getAgentId() {
        return this.agentId;
    }

    public LocalDateTime getTime() {
        return this.time;
    }

    public String getSensorName() {
        return this.sensorName;
    }

    public String getMetricName() {
        return this.metricName;
    }

    public float getValue() {
        return this.value;
    }

    public int getType() {
        return this.type;
    }

    public void setAgentId(UUID agentId) {
        this.agentId = agentId;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Metric other)) return false;
        if (!other.canEqual(this)) return false;
        final Object this$agentId = this.getAgentId();
        final Object other$agentId = other.getAgentId();
        if (!Objects.equals(this$agentId, other$agentId)) return false;
        final Object this$time = this.getTime();
        final Object other$time = other.getTime();
        if (!Objects.equals(this$time, other$time)) return false;
        final Object this$sensorName = this.getSensorName();
        final Object other$sensorName = other.getSensorName();
        if (!Objects.equals(this$sensorName, other$sensorName))
            return false;
        final Object this$metricName = this.getMetricName();
        final Object other$metricName = other.getMetricName();
        if (!Objects.equals(this$metricName, other$metricName))
            return false;
        if (Float.compare(this.getValue(), other.getValue()) != 0) return false;
        return this.getType() == other.getType();
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Metric;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $agentId = this.getAgentId();
        result = result * PRIME + ($agentId == null ? 43 : $agentId.hashCode());
        final Object $time = this.getTime();
        result = result * PRIME + ($time == null ? 43 : $time.hashCode());
        final Object $sensorName = this.getSensorName();
        result = result * PRIME + ($sensorName == null ? 43 : $sensorName.hashCode());
        final Object $metricName = this.getMetricName();
        result = result * PRIME + ($metricName == null ? 43 : $metricName.hashCode());
        result = result * PRIME + Float.floatToIntBits(this.getValue());
        result = result * PRIME + this.getType();
        return result;
    }

    public String toString() {
        return MessageFormat.format("Metric(agentId={0}, time={1}, sensorName={2}, metricName={3}, value={4}, type={5})",
                this.getAgentId(),
                this.getTime(),
                this.getSensorName(),
                this.getMetricName(),
                this.getValue(),
                this.getType());
    }
}
