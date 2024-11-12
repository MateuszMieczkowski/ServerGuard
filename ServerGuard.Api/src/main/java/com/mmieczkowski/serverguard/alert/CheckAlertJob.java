package com.mmieczkowski.serverguard.alert;

import com.mmieczkowski.serverguard.alert.model.Alert;
import com.mmieczkowski.serverguard.alert.model.AlertLog;
import com.mmieczkowski.serverguard.alert.model.AlertMetric;
import com.mmieczkowski.serverguard.metric.MetricRepository;
import com.mmieczkowski.serverguard.resourcegroup.ResourceGroupRepository;
import com.mmieczkowski.serverguard.service.EmailTemplateProvider;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class CheckAlertJob {
    private final AlertRepository alertRepository;
    private final MetricRepository metricRepository;
    private final AlertLogRepository alertLogRepository;
    private final ResourceGroupRepository resourceGroupRepository;
    private final Clock clock;
    private final EmailTemplateProvider emailTemplateProvider;
    private final JavaMailSender mailSender;
    private static final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    public void checkAlerts() {
        log.info("Checking alerts");
        List<Alert> alertToCheck = alertRepository.findAllWhereNextCheckIsNullOrPast();
        alertToCheck.forEach(alert -> {
            try {
                CheckAlert(alert);
            } catch (Exception e) {
                log.error("Error checking alert {}", alert.getId(), e);
            }
        });
    }

    private void CheckAlert(Alert alert) {
        log.info("Checking alert {}", alert.getName());
        float metricValue = getMetricValue(alert);
        if (alert.check(metricValue, clock)) {
            log.info("Alert {} triggered", alert.getName());
            var alertLog = new AlertLog(alert, metricValue, clock);
            alertLogRepository.save(alertLog);
            if (alert.shouldNotify(clock)) {
                try {
                    sendNotification(alertLog);
                } catch (Exception e) {
                    log.error("Error sending notification for alert {}", alert.getId(), e);
                }
                alert.setNextNotification(clock);
            }

        }
        alert.setNextCheck(clock);
        alertRepository.save(alert);
    }

    private float getMetricValue(Alert alert) {
        return (float) metricRepository.findLastMetricValueByAgentId(alert.getAgent().getId(),
                alert.getMetric().getSensorName(),
                alert.getMetric().getMetricName(),
                alert.getMetric().getType(),
                alert.getDuration(),
                alert.getGroupBy().getAggregateFunction());
    }

    private void sendNotification(AlertLog alertLog) {
        var recipientEmails = resourceGroupRepository.findAllResourceGroupUserEmails(alertLog.getAgent().getResourceGroup().getId());
        executorService.submit(() -> {
            try {
                sendEmail(alertLog, recipientEmails.toArray(new String[0]));
            } catch (MessagingException e) {
                log.error("Error sending email for alertLog with id: {}", alertLog.getId(), e);
            }
        });
    }

    private void sendEmail(AlertLog alertLog, String[] emails) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setTo(emails);
        helper.setSubject("Alert Triggered");
        helper.setText(GetFilledTemplate(alertLog), true);
        mailSender.send(mimeMessage);
    }

    private String GetFilledTemplate(AlertLog alertLog) {
        final String templateName = "alertEmailTemplate.html";
        try {
            var template = emailTemplateProvider.getTemplate(templateName);
            template = template.replace("{{alertName}}", alertLog.getAlertName());
            template = template.replace("{{agent.name}}", alertLog.getAgent().getName());
            template = template.replace("{{triggeredAt}}", LocalDateTime.ofInstant(alertLog.getTriggeredAt(), ZoneId.of("UTC")).toString());
            AlertMetric metric = alertLog.getMetric();
            template = template.replace("{{metric}}", String.format("%s %s %s", metric.getSensorName(), metric.getMetricName(), metric.getType()));
            template = template.replace("{{when}}", alertLog.getWhen().getOperator().getOperator());
            template = template.replace("{{groupBy}}", alertLog.getGroupBy().getAggregateFunction());
            template = template.replace("{{threshold}}", String.valueOf(alertLog.getWhen().getThreshold()));
            template = template.replace("{{triggeredByValue}}", String.valueOf(alertLog.getTriggeredByValue()));
            return template;
        } catch (Exception e) {
            log.error("Error getting template {}", templateName, e);
            return "";
        }
    }
}
