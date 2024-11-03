package com.mmieczkowski.serverguard.agent.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateAgentRequest(
        @NotBlank @Size(min = 3, max = 100) String name,
        @NotNull @Valid UpdateAgentConfig config){
    public record UpdateAgentConfig(
            boolean cpuEnabled,
            boolean gpuEnabled,
            boolean memoryEnabled,
            boolean motherboardEnabled,
            boolean controllerEnabled,
            boolean networkEnabled,
            boolean storageEnabled,
            @Min(1) int collectEverySeconds){
    }
}
