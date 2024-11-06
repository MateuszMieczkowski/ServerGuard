package com.mmieczkowski.serverguard.agent;

import com.mmieczkowski.serverguard.config.CacheConstants;
import com.mmieczkowski.serverguard.agent.model.Agent;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface AgentRepository extends JpaRepository<Agent, UUID> {

    Optional<Agent> findAgentByAgentConfigApiKey(String apiKey);


    @Query("SELECT a FROM Agent a " +
            "WHERE a.id = :agentId " +
            "AND EXISTS (SELECT 1 FROM UserResourceGroupPermission up WHERE up.user.id = :userId AND up.resourceGroup.id = a.resourceGroup.id)")
    Optional<Agent> findAgentByIdAndUserId(UUID agentId, UUID userId);

    Page<Agent> findAgentsByResourceGroupId(UUID resourceGroupId, Pageable pageable);

    @Override
    @Modifying
    @Query("UPDATE Agent a SET a.isDeleted = true WHERE a = :agent")
    void delete(@NotNull Agent agent);
}
