package com.mmieczkowski.serverguard.agent;

import com.mmieczkowski.serverguard.agent.request.CreateAgentRequest;
import com.mmieczkowski.serverguard.agent.request.GetAgentsPaginatedRequest;
import com.mmieczkowski.serverguard.agent.request.UpdateAgentRequest;
import com.mmieczkowski.serverguard.agent.response.CreateAgentResponse;
import com.mmieczkowski.serverguard.agent.response.GetAgentResponse;
import com.mmieczkowski.serverguard.agent.response.GetAgentsPaginatedResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/resourceGroups/{resourceGroupId}/agents")
public class AgentController {
    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @PostMapping
    public CreateAgentResponse createAgent(@PathVariable UUID resourceGroupId,
                                           @Validated @RequestBody CreateAgentRequest request) {
        return agentService.createAgent(resourceGroupId, request);
    }

    @GetMapping("/{agentId}")
    public GetAgentResponse getAgent(@PathVariable UUID resourceGroupId, @PathVariable UUID agentId) {
        return agentService.getAgent(resourceGroupId, agentId);
    }

    @PutMapping("/{agentId}")
    public void updateAgent(@PathVariable UUID resourceGroupId,
                            @PathVariable UUID agentId,
                            @Validated @RequestBody UpdateAgentRequest request) {
        agentService.updateAgent(resourceGroupId, agentId, request);
    }

    @GetMapping
    public GetAgentsPaginatedResponse getAgentsPaginated(@PathVariable UUID resourceGroupId,
                                                         GetAgentsPaginatedRequest request) {
        return agentService.getAgentsPaginated(resourceGroupId, request);
    }

    @DeleteMapping("/{agentId}")
    public void deleteAgent(@PathVariable UUID resourceGroupId, @PathVariable UUID agentId) {
        agentService.deleteAgent(resourceGroupId, agentId);
    }
}
