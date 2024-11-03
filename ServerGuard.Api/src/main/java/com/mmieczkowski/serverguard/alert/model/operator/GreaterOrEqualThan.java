package com.mmieczkowski.serverguard.alert.model.operator;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embeddable;

@Embeddable
@DiscriminatorValue(">=")
public class GreaterOrEqualThan extends Operator{
    @Override
    public boolean evaluate(float value, float threshold) {
        return value >= threshold;
    }

    @Override
    public String getOperator() {
        return ">=";
    }
}
