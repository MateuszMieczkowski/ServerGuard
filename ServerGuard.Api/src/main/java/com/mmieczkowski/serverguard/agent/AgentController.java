package com.mmieczkowski.serverguard.agent;

import com.mmieczkowski.serverguard.agent.request.CreateAgentRequest;
import com.mmieczkowski.serverguard.agent.response.CreateAgentResponse;
import com.mmieczkowski.serverguard.agent.request.GetAgentRequest;
import com.mmieczkowski.serverguard.agent.response.GetAgentResponse;
import com.mmieczkowski.serverguard.metric.response.GetAvailableMetricsResponse;
import com.mmieczkowski.serverguard.agent.request.GetAgentsPaginatedRequest;
import com.mmieczkowski.serverguard.agent.response.GetAgentsPaginatedResponse;
import com.mmieczkowski.serverguard.agent.request.UpdateAgentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
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

    @GetMapping("/{agentId}")
    public GetAgentResponse getAgent(@PathVariable UUID resourceGroupId, @PathVariable UUID agentId) {
        return agentService.getAgent(new GetAgentRequest(resourceGroupId, agentId));
    }

    @PutMapping("/{agentId}")
    public void updateAgent(@PathVariable UUID resourceGroupId,
                            @PathVariable UUID agentId,
                            @Validated @RequestBody UpdateAgentRequest request) {
        agentService.updateAgent(agentId, request);
    }

    @GetMapping
    public GetAgentsPaginatedResponse getAgentsPaginated(@PathVariable UUID resourceGroupId, GetAgentsPaginatedRequest request) {
        return agentService.getAgentsPaginated(resourceGroupId, request);
    }

    @GetMapping("/{agentId}/config")
    public ResponseEntity<InputStreamResource> getAgentConfigFile(@PathVariable UUID resourceGroupId, @PathVariable UUID agentId) {
        byte[] bytes = agentService.getAgentConfigFileAsJson(resourceGroupId, agentId);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=agent.config.json");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        InputStream inputStream = new ByteArrayInputStream(bytes);
        InputStreamResource resource = new InputStreamResource(inputStream);
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }
}
