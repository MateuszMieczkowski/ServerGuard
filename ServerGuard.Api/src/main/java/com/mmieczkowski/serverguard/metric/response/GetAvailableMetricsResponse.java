package com.mmieczkowski.serverguard.metric.response;

import com.mmieczkowski.serverguard.metric.model.MetricType;

import java.util.List;

public record GetAvailableMetricsResponse(List<Sensor> sensors) {
    public record Sensor(String name, List<Metric> metrics) {
        public record Metric(String name, List<MetricType> types) {
        }
    }
}
