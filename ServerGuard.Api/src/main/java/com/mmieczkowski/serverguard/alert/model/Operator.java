package com.mmieczkowski.serverguard.alert.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.Locale;

@Embeddable
public class Operator {

    private static final String[] operators = { ">", "<", ">=", "<=", "==", "!="};

    @Column(nullable = false)
    @Getter
    private String operator;

    public Operator() {
    }

    public Operator(String operator) {
        this.operator = operator;
    }

    public boolean evaluate(float value, float threshold){
        ExpressionParser parser = new SpelExpressionParser();

        // Evaluate this expression to boolean {value} {operator} {threshold}
        Expression exp = parser.parseExpression(String.format(Locale.ENGLISH, "%f %s %f", value, operator, threshold));
        Boolean result = exp.getValue(Boolean.class);
        return result != null && result;
    }
}
