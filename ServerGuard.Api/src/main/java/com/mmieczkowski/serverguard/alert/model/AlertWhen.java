package com.mmieczkowski.serverguard.alert.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

@Embeddable
public class AlertWhen {

    @Column(nullable = false)
    private float threshold;

    @Embedded
    private Operator operator;

    public AlertWhen(float threshold, Operator operator) {
        this.threshold = threshold;
        this.operator = operator;
    }

    public AlertWhen() {

    }

    public boolean evaluate(float value) {
        return operator.evaluate(value, this.threshold);
    }

    public float getThreshold() {
        return this.threshold;
    }

    public Operator getOperator() {
        return this.operator;
    }
}
