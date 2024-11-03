package com.mmieczkowski.serverguard.metric;

import java.time.LocalDateTime;

public record DataPoint(LocalDateTime time, double value) {
}
