package com.mmieczkowski.serverguard.alert;

import com.mmieczkowski.serverguard.agent.AgentRepository;
import com.mmieczkowski.serverguard.agent.exception.AgentNotFoundException;
import com.mmieczkowski.serverguard.agent.model.Agent;
import com.mmieczkowski.serverguard.alert.model.*;
import com.mmieczkowski.serverguard.alert.request.CreateAlertRequest;
import com.mmieczkowski.serverguard.alert.request.GetAlertLogsPageRequest;
import com.mmieczkowski.serverguard.alert.request.GetAlertsPageRequest;
import com.mmieczkowski.serverguard.alert.response.GetAlertLogsPageResponse;
import com.mmieczkowski.serverguard.alert.response.GetAlertsPageResponse;
import com.mmieczkowski.serverguard.annotation.ResourceGroupAccess;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AlertService {
    private final AgentRepository agentRepository;
    private final AlertRepository alertRepository;
    private final AlertLogRepository alertLogRepository;

    public AlertService(AgentRepository agentRepository,
                        AlertRepository alertRepository,
                        AlertLogRepository alertLogRepository) {
        this.agentRepository = agentRepository;
        this.alertRepository = alertRepository;
        this.alertLogRepository = alertLogRepository;
    }
    @ResourceGroupAccess
    public void createAlert(UUID resourceGroupId, UUID agentId, CreateAlertRequest request) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(AgentNotFoundException::new);
        AlertMetric metric = new AlertMetric(request.metric().sensorName(), request.metric().metricName(), request.metric().type());
        Operator operator = new Operator(request.operator());
        AlertWhen when = new AlertWhen(request.threshold(), operator);
        GroupBy groupBy = new GroupBy(request.groupBy());
        Alert alert = new Alert(request.name(), agent, metric, when, groupBy, request.duration());
        alertRepository.save(alert);
    }

    @ResourceGroupAccess
    public GetAlertLogsPageResponse getAlertLogsPage(UUID resourceGroupId, UUID agentId, GetAlertLogsPageRequest request) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(AgentNotFoundException::new);
        var pageable = PageRequest.of(request.pageNumber(),
                request.pageSize(),
                Sort.by(Sort.Order.desc("triggeredAt")));
        Page<AlertLog> alertLogsPage = alertLogRepository.findAllByAgentId(agent.getId(), pageable);
        Page<GetAlertLogsPageResponse.AlertLog> responsePage = alertLogsPage.map(alertLog -> new GetAlertLogsPageResponse.AlertLog(
                alertLog.getId(),
                alertLog.getAlert() != null ? alertLog.getAlert().getId() : null,
                alertLog.getAlertName(),
                alertLog.getMetric().getSensorName(),
                alertLog.getMetric().getMetricName(),
                alertLog.getMetric().getType(),
                alertLog.getWhen().getThreshold(),
                alertLog.getWhen().getOperator().getOperator(),
                alertLog.getDuration(),
                alertLog.getGroupBy().getAggregateFunction(),
                alertLog.getTriggeredAt(),
                alertLog.getTriggeredByValue()
        ));
        return new GetAlertLogsPageResponse(responsePage);
    }

    @ResourceGroupAccess
    public void deleteAlert(UUID resourceGroupId, UUID agentId, UUID alertId) {
        agentRepository.findById(agentId)
                .orElseThrow(AgentNotFoundException::new);
        Optional<Alert> optionalAlert = alertRepository.findById(alertId);
        if (optionalAlert.isEmpty()) {
            return;
        }
        alertRepository.delete(optionalAlert.get());
    }

    @ResourceGroupAccess
    public GetAlertsPageResponse getAlertsPage(UUID resourceGroupId, UUID agentId, GetAlertsPageRequest request) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(AgentNotFoundException::new);
        var pageable = PageRequest.of(request.pageNumber(),
                request.pageSize(),
                Sort.by(Sort.Order.asc("name")));
        Page<Alert> alertsPage = alertRepository.findAllByAgentId(agent.getId(), pageable);
        Page<GetAlertsPageResponse.Alert> responsePage = alertsPage.map(alert -> new GetAlertsPageResponse.Alert(
                alert.getId(),
                alert.getName(),
                alert.getMetric().getSensorName(),
                alert.getMetric().getMetricName(),
                alert.getMetric().getType(),
                alert.getWhen().getThreshold(),
                alert.getWhen().getOperator().getOperator(),
                alert.getDuration().toString(),
                alert.getGroupBy().getAggregateFunction()
        ));
        return new GetAlertsPageResponse(responsePage);
    }
}
