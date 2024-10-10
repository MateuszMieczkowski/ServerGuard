package com.mmieczkowski.serverguard.feature.metric.collect;

import java.time.Instant;
import java.util.List;

public record Metrics(Instant time, List<SensorMetric> sensorMetrics) {
}
