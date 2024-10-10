package com.mmieczkowski.serverguard.feature.metric.collect;

import com.mmieczkowski.serverguard.model.MetricType;

public record Metric(String name, Float value, MetricType type) {
}
