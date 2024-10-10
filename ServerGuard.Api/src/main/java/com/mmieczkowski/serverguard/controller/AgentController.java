package com.mmieczkowski.serverguard.controller;

import com.mmieczkowski.serverguard.feature.agent.create.CreateAgentRequest;
import com.mmieczkowski.serverguard.feature.agent.create.CreateAgentResponse;
import com.mmieczkowski.serverguard.feature.agent.service.AgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/resourceGroups/{resourceGroupId}/agents")
@RequiredArgsConstructor
public class AgentController {
    private final AgentService agentService;

    @PostMapping
    public CreateAgentResponse createAgent(@PathVariable UUID resourceGroupId,
                                           @Validated @RequestBody CreateAgentRequest request) {
        return agentService.createAgent(resourceGroupId, request);
    }
}
