package com.mmieczkowski.serverguard.dashboard.response;

import com.mmieczkowski.serverguard.metric.model.MetricType;

import java.util.List;

public record GetDashboardResponse(String name, List<Graph> graphs) {
    public record Graph(int index, String sensorName, String metricName, MetricType metricType, String lineColor, String unit) {
    }
}
