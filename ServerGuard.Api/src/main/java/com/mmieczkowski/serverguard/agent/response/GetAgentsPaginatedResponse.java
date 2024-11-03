package com.mmieczkowski.serverguard.agent.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GetAgentsPaginatedResponse(List<Agent> agents, int pageNumber, int pageSize, int totalPages) {
    public record Agent(UUID id, String name, Instant lastContactAt) {
    }
}
