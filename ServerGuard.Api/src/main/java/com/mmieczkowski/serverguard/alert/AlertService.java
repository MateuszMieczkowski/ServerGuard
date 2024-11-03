package com.mmieczkowski.serverguard.alert;

import com.mmieczkowski.serverguard.agent.AgentRepository;
import com.mmieczkowski.serverguard.agent.exception.AgentNotFoundException;
import com.mmieczkowski.serverguard.agent.model.Agent;
import com.mmieczkowski.serverguard.alert.model.Alert;
import com.mmieczkowski.serverguard.alert.model.AlertMetric;
import com.mmieczkowski.serverguard.alert.model.AlertWhen;
import com.mmieczkowski.serverguard.alert.model.groupby.GroupBy;
import com.mmieczkowski.serverguard.alert.model.operator.Operator;
import com.mmieczkowski.serverguard.alert.request.CreateAlertRequest;
import com.mmieczkowski.serverguard.resourcegroup.exception.ResourceGroupNotFoundException;
import com.mmieczkowski.serverguard.service.UserService;
import com.mmieczkowski.serverguard.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlertService {
    private final AgentRepository agentRepository;
    private final UserService userService;
    private final AlertRepository alertRepository;

    public void createAlert(UUID resourceGroupId, UUID agentId, CreateAlertRequest request) {
        User user = userService.getLoggedInUser()
                .orElseThrow();
        if(!user.hasAccessToResourceGroup(resourceGroupId))
        {
            throw new ResourceGroupNotFoundException(resourceGroupId);
        }
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(AgentNotFoundException::new);
        AlertMetric metric = new AlertMetric(request.metric().sensorName(), request.metric().metricName(), request.metric().type());
        Operator operator = Operator.create(request.operator());
        AlertWhen when = new AlertWhen(request.threshold(), operator);
        GroupBy groupBy = GroupBy.create(request.groupBy());
        Alert alert = new Alert(request.name(), agent, metric,when, groupBy, request.duration());
        alertRepository.save(alert);
    }
}
