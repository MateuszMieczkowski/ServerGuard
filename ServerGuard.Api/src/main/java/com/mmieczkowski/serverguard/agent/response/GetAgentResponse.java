package com.mmieczkowski.serverguard.agent.response;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

public record GetAgentResponse(UUID id, String name, AgentConfig config, Instant lastContactAt) {
    @Data
    public static class AgentConfig {

        public AgentConfig(com.mmieczkowski.serverguard.agent.model.AgentConfig agentConfig){
            this.isCpuEnabled = agentConfig.isCpuEnabled();
            this.isGpuEnabled = agentConfig.isGpuEnabled();
            this.isMemoryEnabled = agentConfig.isMemoryEnabled();
            this.isMotherboardEnabled = agentConfig.isMotherboardEnabled();
            this.isControllerEnabled = agentConfig.isControllerEnabled();
            this.isNetworkEnabled = agentConfig.isNetworkEnabled();
            this.isStorageEnabled = agentConfig.isStorageEnabled();
            this.collectEverySeconds = agentConfig.getCollectEverySeconds();
            this.apiKey = agentConfig.getApiKey();
        }

        private boolean isCpuEnabled;

        private boolean isGpuEnabled;

        private boolean isMemoryEnabled;

        private boolean isMotherboardEnabled;

        private boolean isControllerEnabled;

        private boolean isNetworkEnabled;

        private boolean isStorageEnabled;

        private int collectEverySeconds;

        private String apiKey;
    }
}
