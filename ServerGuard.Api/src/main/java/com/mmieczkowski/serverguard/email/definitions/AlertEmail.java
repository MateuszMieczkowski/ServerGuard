package com.mmieczkowski.serverguard.email.definitions;

import com.mmieczkowski.serverguard.alert.model.AlertLog;
import com.mmieczkowski.serverguard.alert.model.AlertMetric;
import com.mmieczkowski.serverguard.email.EmailDefinition;
import com.mmieczkowski.serverguard.email.EmailPlaceHolder;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class AlertEmail extends EmailDefinition {
    private final String[] recipientEmails;
    private final AlertLog alertLog;

    public AlertEmail(String[] recipientEmails, AlertLog alertLog){
        this.recipientEmails = recipientEmails;
        this.alertLog = alertLog;
    }
    @Override
    public String getTemplateFileName() {
        return "resetPasswordEmail";
    }

    @Override
    public String getSubject() {
        return "Alert Triggered";
    }

    @Override
    public EmailPlaceHolder[] getPlaceHolders() {
        AlertMetric metric = alertLog.getMetric();
        return new EmailPlaceHolder[]{
                new EmailPlaceHolder("{{alertName}}", alertLog.getAlertName()),
                new EmailPlaceHolder("{{agent.name}}", alertLog.getAgent().getName()),
                new EmailPlaceHolder("{{triggeredAt}}", LocalDateTime.ofInstant(alertLog.getTriggeredAt(), ZoneId.of("UTC")).toString()),
                new EmailPlaceHolder("{{metric}}", String.format("%s %s %s", metric.getSensorName(), metric.getMetricName(), metric.getType())),
                new EmailPlaceHolder("{{when}}", alertLog.getWhen().getOperator().getOperator()),
                new EmailPlaceHolder("{{groupBy}}", alertLog.getGroupBy().getAggregateFunction()),
                new EmailPlaceHolder("{{threshold}}", String.valueOf(alertLog.getWhen().getThreshold())),
                new EmailPlaceHolder("{{triggeredByValue}}", String.valueOf(alertLog.getTriggeredByValue()))
        };
    }

    @Override
    public String[] getRecipients() {
        return recipientEmails;
    }
}
