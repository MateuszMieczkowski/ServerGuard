package com.mmieczkowski.serverguard.alert;

import com.mmieczkowski.serverguard.alert.request.CreateAlertRequest;
import com.mmieczkowski.serverguard.alert.request.GetAlertLogsPageRequest;
import com.mmieczkowski.serverguard.alert.request.GetAlertsPageRequest;
import com.mmieczkowski.serverguard.alert.response.GetAlertLogsPageResponse;
import com.mmieczkowski.serverguard.alert.response.GetAlertsPageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/resourceGroups/{resourceGroupId}/agents/{agentId}/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @PostMapping
    public void createAlert(@PathVariable UUID resourceGroupId, @PathVariable UUID agentId, @RequestBody @Valid CreateAlertRequest createAlertRequest) {
        alertService.createAlert(resourceGroupId, agentId, createAlertRequest);
    }

    @GetMapping
    public GetAlertsPageResponse getAlertsPage(@PathVariable UUID resourceGroupId, @PathVariable UUID agentId, @Validated GetAlertsPageRequest request) {
        return alertService.getAlertsPage(resourceGroupId, agentId, request);
    }

    @DeleteMapping("/{alertId}")
    public void deleteAlert(@PathVariable UUID resourceGroupId, @PathVariable UUID agentId, @PathVariable UUID alertId) {
        alertService.deleteAlert(resourceGroupId, agentId, alertId);
    }

    @GetMapping("/logs")
    public GetAlertLogsPageResponse getAlertLogsPage(@PathVariable UUID resourceGroupId, @PathVariable UUID agentId, @Validated GetAlertLogsPageRequest request) {
        return alertService.getAlertLogsPage(resourceGroupId, agentId, request);
    }
}
