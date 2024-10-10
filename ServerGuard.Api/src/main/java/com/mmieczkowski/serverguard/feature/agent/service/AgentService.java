package com.mmieczkowski.serverguard.feature.agent.service;

import com.mmieczkowski.serverguard.feature.agent.create.CreateAgentRequest;
import com.mmieczkowski.serverguard.feature.agent.create.CreateAgentResponse;
import com.mmieczkowski.serverguard.feature.resourceGroup.exception.ResourceGroupNotFoundException;
import com.mmieczkowski.serverguard.model.Agent;
import com.mmieczkowski.serverguard.model.User;
import com.mmieczkowski.serverguard.repository.AgentRepository;
import com.mmieczkowski.serverguard.repository.ResourceGroupRepository;
import com.mmieczkowski.serverguard.service.SecureRandomString;
import com.mmieczkowski.serverguard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AgentService {

    private final AgentRepository agentRepository;
    private final ResourceGroupRepository ResourceGroupRepository;
    private final UserService userService;

    @Transactional
    public CreateAgentResponse createAgent(UUID resourceGroupId, CreateAgentRequest request) {
        User user = userService.getLoggedInUser()
                .orElseThrow();
        if (!user.hasAccessToResourceGroup(resourceGroupId)) {
            throw new ResourceGroupNotFoundException(resourceGroupId);
        }
        var resourceGroup = ResourceGroupRepository.findById(resourceGroupId)
                .orElseThrow(() -> new ResourceGroupNotFoundException(resourceGroupId));
        var agentConfig = request.agentConfig().toAgentConfig();
        agentConfig.setApiKey(SecureRandomString.generate(40));
        Agent agent = new Agent(request.name(), resourceGroup, agentConfig);
        agentRepository.save(agent);
        return new CreateAgentResponse(agent.getId(), agent.getName());
    }
}
