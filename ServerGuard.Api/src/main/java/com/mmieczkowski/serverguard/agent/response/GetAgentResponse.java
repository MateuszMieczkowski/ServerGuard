package com.mmieczkowski.serverguard.agent.response;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record GetAgentResponse(UUID id, String name, AgentConfig config, Instant lastContactAt) {
    public static class AgentConfig {

        public AgentConfig(com.mmieczkowski.serverguard.agent.model.AgentConfig agentConfig) {
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

        private final boolean isCpuEnabled;

        private final boolean isGpuEnabled;

        private final boolean isMemoryEnabled;

        private final boolean isMotherboardEnabled;

        private final boolean isControllerEnabled;

        private final boolean isNetworkEnabled;

        private final boolean isStorageEnabled;

        private final int collectEverySeconds;

        private final String apiKey;

        public boolean isCpuEnabled() {
            return this.isCpuEnabled;
        }

        public boolean isGpuEnabled() {
            return this.isGpuEnabled;
        }

        public boolean isMemoryEnabled() {
            return this.isMemoryEnabled;
        }

        public boolean isMotherboardEnabled() {
            return this.isMotherboardEnabled;
        }

        public boolean isControllerEnabled() {
            return this.isControllerEnabled;
        }

        public boolean isNetworkEnabled() {
            return this.isNetworkEnabled;
        }

        public boolean isStorageEnabled() {
            return this.isStorageEnabled;
        }

        public int getCollectEverySeconds() {
            return this.collectEverySeconds;
        }

        public String getApiKey() {
            return this.apiKey;
        }

        public boolean equals(final Object o) {
            if (o == this) return true;
            if (!(o instanceof AgentConfig other)) return false;
            if (!other.canEqual(this)) return false;
            if (this.isCpuEnabled() != other.isCpuEnabled()) return false;
            if (this.isGpuEnabled() != other.isGpuEnabled()) return false;
            if (this.isMemoryEnabled() != other.isMemoryEnabled()) return false;
            if (this.isMotherboardEnabled() != other.isMotherboardEnabled()) return false;
            if (this.isControllerEnabled() != other.isControllerEnabled()) return false;
            if (this.isNetworkEnabled() != other.isNetworkEnabled()) return false;
            if (this.isStorageEnabled() != other.isStorageEnabled()) return false;
            if (this.getCollectEverySeconds() != other.getCollectEverySeconds()) return false;
            final Object this$apiKey = this.getApiKey();
            final Object other$apiKey = other.getApiKey();
            return Objects.equals(this$apiKey, other$apiKey);
        }

        protected boolean canEqual(final Object other) {
            return other instanceof AgentConfig;
        }

        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            result = result * PRIME + (this.isCpuEnabled() ? 79 : 97);
            result = result * PRIME + (this.isGpuEnabled() ? 79 : 97);
            result = result * PRIME + (this.isMemoryEnabled() ? 79 : 97);
            result = result * PRIME + (this.isMotherboardEnabled() ? 79 : 97);
            result = result * PRIME + (this.isControllerEnabled() ? 79 : 97);
            result = result * PRIME + (this.isNetworkEnabled() ? 79 : 97);
            result = result * PRIME + (this.isStorageEnabled() ? 79 : 97);
            result = result * PRIME + this.getCollectEverySeconds();
            final Object $apiKey = this.getApiKey();
            result = result * PRIME + ($apiKey == null ? 43 : $apiKey.hashCode());
            return result;
        }

        public String toString() {
            return "GetAgentResponse.AgentConfig(isCpuEnabled=" + this.isCpuEnabled() + ", isGpuEnabled=" + this.isGpuEnabled() + ", isMemoryEnabled=" + this.isMemoryEnabled() + ", isMotherboardEnabled=" + this.isMotherboardEnabled() + ", isControllerEnabled=" + this.isControllerEnabled() + ", isNetworkEnabled=" + this.isNetworkEnabled() + ", isStorageEnabled=" + this.isStorageEnabled() + ", collectEverySeconds=" + this.getCollectEverySeconds() + ", apiKey=" + this.getApiKey() + ")";
        }
    }
}
