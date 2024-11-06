package com.mmieczkowski.serverguard.metric.response;


import java.util.List;

public record GetAvailableMetricsResponse(List<Sensor> sensors) {
    public record Sensor(String name, List<Metric> metrics) {
        public record Metric(String name, List<MetricType> types) {
            public record MetricType(String name, String unit) {
            }
        }
    }
}
