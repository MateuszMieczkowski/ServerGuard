package com.mmieczkowski.serverguard.alert.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class GroupBy {

    @Column(nullable = false)
    private String aggregateFunction;

    public GroupBy() {
    }

    public GroupBy(String aggregateFunction) {
        this.aggregateFunction = aggregateFunction;
    }

    public String getAggregateFunction() {
        return this.aggregateFunction;
    }
}
