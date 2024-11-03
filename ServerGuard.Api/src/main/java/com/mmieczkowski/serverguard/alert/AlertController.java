package com.mmieczkowski.serverguard.alert;

import com.mmieczkowski.serverguard.alert.request.CreateAlertRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
}
