package com.mmieczkowski.serverguard.alert.request;

import com.mmieczkowski.serverguard.metric.model.MetricType;

import java.time.Duration;

public record CreateAlertRequest(String name, Metric metric, float threshold, String operator, Duration duration, String groupBy) {
    public record Metric(String sensorName, String metricName, MetricType type) {
    }
}
