package com.mmieczkowski.serverguard.metric.model;

import lombok.Getter;

@Getter
public enum MetricType {
    VOLTAGE(1, "V"),
    CURRENT(2, "A"),
    POWER(3, "W"),
    CLOCK(4, "MHz"),
    TEMPERATURE(5, "°C"),
    LOAD(6, "%"),
    FREQUENCY(7, "Hz"),
    FAN(8, "RPM"),
    FLOW(9, ""),
    CONTROL(10, ""),
    LEVEL(11,"%"),
    FACTOR(12, ""),
    DATA(13, "GB"),
    SMALL_DATA(14, "MB"),
    THROUGHPUT(15, "KB/s"),
    TIME_SPAN(16, ""),
    ENERGY(17, ""),
    NOISE(18, "");

    private final int value;

    private final String unit;

    MetricType(final int value, final String unit) {
        this.value = value;
        this.unit = unit;
    }

}