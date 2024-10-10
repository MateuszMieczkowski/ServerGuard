package com.mmieczkowski.serverguard.feature.agent.create;

import com.mmieczkowski.serverguard.model.AgentConfig;
import jakarta.validation.constraints.NotBlank;

public record CreateAgentConfig(
        boolean isCpuEnabled,
        boolean isGpuEnabled,
        boolean isMemoryEnabled,
        boolean isMotherboardEnabled,
        boolean isControllerEnabled,
        boolean isNetworkEnabled,
        boolean isStorageEnabled,
        @NotBlank int collectEverySeconds
) {
    public AgentConfig toAgentConfig() {
        return new AgentConfig(isCpuEnabled, isGpuEnabled, isMemoryEnabled, isMotherboardEnabled, isControllerEnabled, isNetworkEnabled, isStorageEnabled, collectEverySeconds);
    }
}
