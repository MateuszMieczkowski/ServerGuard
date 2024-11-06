package com.mmieczkowski.serverguard.alert.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
public class GroupBy {
    private static final String[] aggregateFunctions = { "AVG", "MIN", "MAX"};

    @Column(nullable = false)
    @Getter
    private String aggregateFunction;

    public GroupBy() {
    }

    public GroupBy(String aggregateFunction) {
        this.aggregateFunction = aggregateFunction;
    }
}
