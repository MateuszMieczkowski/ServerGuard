package com.mmieczkowski.serverguard.agent;

import com.mmieczkowski.serverguard.agent.model.Agent;
import com.mmieczkowski.serverguard.agent.model.AgentConfig;
import com.mmieczkowski.serverguard.agent.request.CreateAgentRequest;
import com.mmieczkowski.serverguard.agent.response.CreateAgentResponse;
import com.mmieczkowski.serverguard.agent.exception.AgentNotFoundException;
import com.mmieczkowski.serverguard.agent.request.GetAgentRequest;
import com.mmieczkowski.serverguard.agent.response.GetAgentConfigResponse;
import com.mmieczkowski.serverguard.agent.response.GetAgentResponse;
import com.mmieczkowski.serverguard.agent.request.GetAgentsPaginatedRequest;
import com.mmieczkowski.serverguard.agent.response.GetAgentsPaginatedResponse;
import com.mmieczkowski.serverguard.agent.request.UpdateAgentRequest;
import com.mmieczkowski.serverguard.resourcegroup.exception.ResourceGroupNotFoundException;
import com.mmieczkowski.serverguard.user.User;
import com.mmieczkowski.serverguard.metric.MetricRepository;
import com.mmieczkowski.serverguard.resourcegroup.ResourceGroupRepository;
import com.mmieczkowski.serverguard.service.SecureRandomString;
import com.mmieczkowski.serverguard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AgentService {

    private final AgentRepository agentRepository;
    private final ResourceGroupRepository ResourceGroupRepository;
    private final UserService userService;
    private final MetricRepository metricRepository;

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

    public GetAgentResponse getAgent(GetAgentRequest request) {
        UUID userId = userService.getLoggedInUser()
                .orElseThrow().getId();
        Agent agent = agentRepository.findAgentByIdAndUserId(request.id(), userId)
                .orElseThrow(AgentNotFoundException::new);
        GetAgentResponse.AgentConfig agentConfig = new GetAgentResponse.AgentConfig(agent.getAgentConfig());
        return new GetAgentResponse(agent.getId(), agent.getName(), agentConfig, agent.getLastContactAt());
    }

    public void updateAgent(UUID agentId, UpdateAgentRequest request) {
        UUID userId = userService.getLoggedInUser()
                .orElseThrow().getId();
        Agent agent = agentRepository.findAgentByIdAndUserId(agentId, userId)
                .orElseThrow(AgentNotFoundException::new);
        agent.setName(request.name());
        var newAgentConfig = request.agentConfig().toAgentConfig();
        newAgentConfig.setApiKey(agent.getAgentConfig().getApiKey());
        agent.setAgentConfig(newAgentConfig);
        agentRepository.save(agent);
    }

    public GetAgentsPaginatedResponse getAgentsPaginated(UUID resourceGroupId, GetAgentsPaginatedRequest request) {
        User user = userService.getLoggedInUser()
                .orElseThrow();
        if (!user.hasAccessToResourceGroup(resourceGroupId)) {
            throw new ResourceGroupNotFoundException(resourceGroupId);
        }
        var pageable = PageRequest.of(request.pageNumber(), request.pageSize())
                .withSort(Sort.by("name"));
        var agentsPage = agentRepository.findAgentsByResourceGroupId(resourceGroupId, pageable);
        var agents = agentsPage.getContent()
                .stream()
                .map(agent -> new GetAgentsPaginatedResponse.Agent(agent.getId(), agent.getName(), agent.getLastContactAt()))
                .toList();
        return new GetAgentsPaginatedResponse(agents, request.pageNumber(), request.pageSize(), agentsPage.getTotalPages());
    }

    public void deleteAgent(UUID resourceGroupId, UUID agentId) {
        User user = userService.getLoggedInUser()
                .orElseThrow();
        if (!user.hasAccessToResourceGroup(resourceGroupId)) {
            throw new ResourceGroupNotFoundException(resourceGroupId);
        }
        UUID userId = userService.getLoggedInUser()
                .orElseThrow().getId();
        var agentOptional = agentRepository.findAgentByIdAndUserId(agentId, userId);
        if (agentOptional.isEmpty()) {
            return;
        }
        agentRepository.delete(agentOptional.get());
    }

    public GetAgentConfigResponse getAgentConfig(String apiKey) {
        Agent agent = agentRepository.findAgentByAgentConfigApiKey(apiKey)
                .orElseThrow(AgentNotFoundException::new);
        return new GetAgentConfigResponse(agent.getResourceGroup().getId(),
                agent.getId(),
                agent.getAgentConfig().isCpuEnabled(),
                agent.getAgentConfig().isGpuEnabled(),
                agent.getAgentConfig().isMemoryEnabled(),
                agent.getAgentConfig().isMotherboardEnabled(),
                agent.getAgentConfig().isControllerEnabled(),
                agent.getAgentConfig().isNetworkEnabled(),
                agent.getAgentConfig().isStorageEnabled(),
                agent.getAgentConfig().getCollectEverySeconds());
    }
}
