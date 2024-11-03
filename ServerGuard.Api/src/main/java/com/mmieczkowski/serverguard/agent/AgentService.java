package com.mmieczkowski.serverguard.agent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mmieczkowski.serverguard.agent.model.Agent;
import com.mmieczkowski.serverguard.config.CacheConstants;
import com.mmieczkowski.serverguard.agent.request.CreateAgentRequest;
import com.mmieczkowski.serverguard.agent.response.CreateAgentResponse;
import com.mmieczkowski.serverguard.agent.exception.AgentNotFoundException;
import com.mmieczkowski.serverguard.agent.request.GetAgentRequest;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
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
        agent.getAgentConfig().setControllerEnabled(request.config().controllerEnabled());
        agent.getAgentConfig().setCpuEnabled(request.config().cpuEnabled());
        agent.getAgentConfig().setGpuEnabled(request.config().gpuEnabled());
        agent.getAgentConfig().setMemoryEnabled(request.config().memoryEnabled());
        agent.getAgentConfig().setMotherboardEnabled(request.config().motherboardEnabled());
        agent.getAgentConfig().setNetworkEnabled(request.config().networkEnabled());
        agent.getAgentConfig().setStorageEnabled(request.config().storageEnabled());
        agent.getAgentConfig().setCollectEverySeconds(request.config().collectEverySeconds());
        agentRepository.save(agent);

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

    public byte[] getAgentConfigFileAsJson(UUID resourceGroupId, UUID agentId) {
        User user = userService.getLoggedInUser()
                .orElseThrow();
        if (!user.hasAccessToResourceGroup(resourceGroupId)) {
            throw new ResourceGroupNotFoundException(resourceGroupId);
        }
        Agent agent = agentRepository.findAgentByIdAndUserId(agentId, user.getId())
                .orElseThrow(AgentNotFoundException::new);
        var config = agent.getAgentConfig();
        var responseConfig = new GetAgentResponse.AgentConfig(config);
        var objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(responseConfig);
            return json.getBytes();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }



}
