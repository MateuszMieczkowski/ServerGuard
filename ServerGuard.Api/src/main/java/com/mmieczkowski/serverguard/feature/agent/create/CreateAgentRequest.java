package com.mmieczkowski.serverguard.feature.agent.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateAgentRequest(
        @NotBlank @Size(min = 3, max = 100) String name,
        @NotNull CreateAgentConfig agentConfig
) {
}

