package com.mmieczkowski.serverguard.model;

public enum MetricType {
    VOLTAGE(1),
    CURRENT(2),
    POWER(3),
    CLOCK(4),
    TEMPERATURE(5),
    LOAD(6),
    FREQUENCY(7),
    FAN(8),
    FLOW(9),
    CONTROL(10),
    LEVEL(11),
    FACTOR(12),
    DATA(13),
    SMALL_DATA(14),
    THROUGHPUT(15),
    TIME_SPAN(16),
    ENERGY(17),
    NOISE(18);

    private final int value;
    MetricType(final int value){
        this.value = value;
    }

    public int getValue() { return value; }
}