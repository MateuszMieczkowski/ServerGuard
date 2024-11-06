package com.mmieczkowski.serverguard.agent;

import com.mmieczkowski.serverguard.agent.response.GetAgentConfigResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agentConfig")
@RequiredArgsConstructor
public class AgentConfigController {

    private final AgentService agentService;

    @GetMapping
    public GetAgentConfigResponse getAgentConfig(@RequestHeader("x-api-key") String apiKey) {
        return agentService.getAgentConfig(apiKey);
    }
}
