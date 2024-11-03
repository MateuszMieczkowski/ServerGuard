package com.mmieczkowski.serverguard.alert.model.operator;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Embeddable;

@Embeddable
@DiscriminatorColumn(name = "operator")
public class Operator {

    public boolean evaluate(float value, float threshold){
        return false;
    };

    public static Operator create(String operator) {
        return switch (operator) {
            case "<" -> new LessThan();
            case "<=" -> new LessOrEqualThan();
            case ">" -> new GreaterThan();
            case ">=" -> new GreaterOrEqualThan();
            default -> throw new IllegalArgumentException("Invalid operator: " + operator);
        };
    }

    public String getOperator() {
        return "";
    }
}
