package com.mmieczkowski.serverguard.alert;

import com.mmieczkowski.serverguard.alert.model.Alert;
import com.mmieczkowski.serverguard.alert.model.AlertLog;
import com.mmieczkowski.serverguard.email.EmailService;
import com.mmieczkowski.serverguard.email.definitions.AlertEmail;
import com.mmieczkowski.serverguard.metric.MetricRepository;
import com.mmieczkowski.serverguard.resourcegroup.ResourceGroupRepository;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Clock;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class CheckAlertJob {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(CheckAlertJob.class);
    private static final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    private final AlertRepository alertRepository;
    private final MetricRepository metricRepository;
    private final AlertLogRepository alertLogRepository;
    private final ResourceGroupRepository resourceGroupRepository;
    private final Clock clock;
    private final EmailService emailService;

    public CheckAlertJob(AlertRepository alertRepository,
                         MetricRepository metricRepository,
                         AlertLogRepository alertLogRepository,
                         ResourceGroupRepository resourceGroupRepository,
                         Clock clock,
                         EmailService emailService) {
        this.alertRepository = alertRepository;
        this.metricRepository = metricRepository;
        this.alertLogRepository = alertLogRepository;
        this.resourceGroupRepository = resourceGroupRepository;
        this.clock = clock;
        this.emailService = emailService;
    }

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
            } catch (MessagingException | IOException e) {
                log.error("Error sending email for alertLog with id: {}", alertLog.getId(), e);
            }
        });
    }

    private void sendEmail(AlertLog alertLog, String[] emails) throws MessagingException, IOException {
        var alertEmail = new AlertEmail(emails, alertLog);
        emailService.sendEmail(alertEmail);
    }
}
