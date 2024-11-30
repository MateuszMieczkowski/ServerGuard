package com.mmieczkowski.serverguard.agent;

import com.mmieczkowski.serverguard.agent.exception.AgentNotFoundException;
import com.mmieczkowski.serverguard.agent.model.Agent;
import com.mmieczkowski.serverguard.agent.request.CreateAgentRequest;
import com.mmieczkowski.serverguard.agent.request.GetAgentsPaginatedRequest;
import com.mmieczkowski.serverguard.agent.request.UpdateAgentRequest;
import com.mmieczkowski.serverguard.agent.response.CreateAgentResponse;
import com.mmieczkowski.serverguard.agent.response.GetAgentConfigResponse;
import com.mmieczkowski.serverguard.agent.response.GetAgentResponse;
import com.mmieczkowski.serverguard.agent.response.GetAgentsPaginatedResponse;
import com.mmieczkowski.serverguard.annotation.ResourceGroupAccess;
import com.mmieczkowski.serverguard.resourcegroup.ResourceGroupRepository;
import com.mmieczkowski.serverguard.resourcegroup.exception.ResourceGroupNotFoundException;
import com.mmieczkowski.serverguard.resourcegroup.model.ResourceGroupUserRole;
import com.mmieczkowski.serverguard.service.SecureRandomString;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AgentService {
    private final AgentRepository agentRepository;
    private final ResourceGroupRepository ResourceGroupRepository;

    public AgentService(AgentRepository agentRepository, ResourceGroupRepository ResourceGroupRepository) {
        this.agentRepository = agentRepository;
        this.ResourceGroupRepository = ResourceGroupRepository;
    }

    @Transactional
    @ResourceGroupAccess(roles = {ResourceGroupUserRole.USER, ResourceGroupUserRole.ADMIN})
    public CreateAgentResponse createAgent(UUID resourceGroupId, CreateAgentRequest request) {
        var resourceGroup = ResourceGroupRepository.findById(resourceGroupId)
                .orElseThrow(() -> new ResourceGroupNotFoundException(resourceGroupId));
        var agentConfig = request.agentConfig().toAgentConfig();
        agentConfig.setApiKey(SecureRandomString.generate(40));
        Agent agent = new Agent(request.name(), resourceGroup, agentConfig);
        agentRepository.save(agent);
        return new CreateAgentResponse(agent.getId(), agent.getName());
    }

    @ResourceGroupAccess(roles = {ResourceGroupUserRole.USER, ResourceGroupUserRole.ADMIN})
    public GetAgentResponse getAgent(UUID resourceGroupId, UUID agentId) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(AgentNotFoundException::new);
        GetAgentResponse.AgentConfig agentConfig = new GetAgentResponse.AgentConfig(agent.getAgentConfig());
        return new GetAgentResponse(agent.getId(), agent.getName(), agentConfig, agent.getLastContactAt());
    }

    @ResourceGroupAccess(roles = {ResourceGroupUserRole.USER, ResourceGroupUserRole.ADMIN})
    public void updateAgent(UUID resourceGroupId, UUID agentId, UpdateAgentRequest request) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(AgentNotFoundException::new);
        agent.setName(request.name());
        var newAgentConfig = request.agentConfig().toAgentConfig();
        newAgentConfig.setApiKey(agent.getAgentConfig().getApiKey());
        agent.setAgentConfig(newAgentConfig);
        agentRepository.save(agent);
    }

    @ResourceGroupAccess(roles = {ResourceGroupUserRole.USER, ResourceGroupUserRole.ADMIN})
    public GetAgentsPaginatedResponse getAgentsPaginated(UUID resourceGroupId, GetAgentsPaginatedRequest request) {
        var pageable = PageRequest.of(request.pageNumber(), request.pageSize())
                .withSort(Sort.by("name"));
        var agentsPage = agentRepository.findAgentsByResourceGroupId(resourceGroupId, pageable);
        var agents = agentsPage.getContent()
                .stream()
                .map(agent -> new GetAgentsPaginatedResponse.Agent(agent.getId(), agent.getName(),
                        agent.getLastContactAt()))
                .toList();
        return new GetAgentsPaginatedResponse(agents, request.pageNumber(), request.pageSize(),
                agentsPage.getTotalPages());
    }

    @ResourceGroupAccess(roles = {ResourceGroupUserRole.USER, ResourceGroupUserRole.ADMIN})
    public void deleteAgent(UUID resourceGroupId, UUID agentId) {
        var agentOptional = agentRepository.findById(agentId);
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
