package com.mmieczkowski.serverguard.alert.model;

import com.mmieczkowski.serverguard.alert.model.operator.Operator;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.Getter;

@Embeddable
public class AlertWhen {

    @Getter
    @Column(nullable = false)
    private float threshold;

    @Getter
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
}
