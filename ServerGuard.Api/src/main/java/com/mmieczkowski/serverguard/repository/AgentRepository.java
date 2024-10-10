package com.mmieczkowski.serverguard.repository;

import com.mmieczkowski.serverguard.model.Agent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AgentRepository extends JpaRepository<Agent, UUID> {
    Optional<Agent> findAgentByAgentConfigApiKey(String apiKey);
}
