package com.mmieczkowski.serverguard.metric;

import java.time.Instant;

public record DataPoint(Instant time, double value) {
}
