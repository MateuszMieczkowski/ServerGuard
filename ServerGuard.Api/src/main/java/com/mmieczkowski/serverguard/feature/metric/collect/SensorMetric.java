package com.mmieczkowski.serverguard.feature.metric.collect;

import java.util.List;

public record SensorMetric(String name, List<Metric> metrics) {
}
