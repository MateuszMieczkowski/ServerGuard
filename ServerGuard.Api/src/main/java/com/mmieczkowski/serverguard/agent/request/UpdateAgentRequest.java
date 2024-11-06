package com.mmieczkowski.serverguard.agent.request;

import com.mmieczkowski.serverguard.agent.model.AgentConfig;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateAgentRequest(
        @NotBlank @Size(min = 3, max = 100) String name,
        @NotNull @Valid UpdateAgentConfig agentConfig){
    public record UpdateAgentConfig(
            boolean isCpuEnabled,
            boolean isGpuEnabled,
            boolean isMemoryEnabled,
            boolean isMotherboardEnabled,
            boolean isControllerEnabled,
            boolean isNetworkEnabled,
            boolean isStorageEnabled,
            @Min(1) int collectEverySeconds){
        public AgentConfig toAgentConfig() {
            return new AgentConfig(isCpuEnabled,
                    isGpuEnabled,
                    isMemoryEnabled,
                    isMotherboardEnabled,
                    isControllerEnabled,
                    isNetworkEnabled,
                    isStorageEnabled,
                    collectEverySeconds);
        }
    }
}
