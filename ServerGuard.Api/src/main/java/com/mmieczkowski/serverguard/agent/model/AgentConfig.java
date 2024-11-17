package com.mmieczkowski.serverguard.agent.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class AgentConfig implements Serializable {

    public AgentConfig(boolean isCpuEnabled,
                       boolean isGpuEnabled,
                       boolean isMemoryEnabled,
                       boolean isMotherboardEnabled,
                       boolean isControllerEnabled,
                       boolean isNetworkEnabled,
                       boolean isStorageEnabled,
                       int collectEverySeconds) {
        this.isCpuEnabled = isCpuEnabled;
        this.isGpuEnabled = isGpuEnabled;
        this.isMemoryEnabled = isMemoryEnabled;
        this.isMotherboardEnabled = isMotherboardEnabled;
        this.isControllerEnabled = isControllerEnabled;
        this.isNetworkEnabled = isNetworkEnabled;
        this.isStorageEnabled = isStorageEnabled;
        this.collectEverySeconds = collectEverySeconds;
    }

    public AgentConfig() {
    }

    @Column(nullable = false)
    private boolean isCpuEnabled;

    @Column(nullable = false)
    private boolean isGpuEnabled;

    @Column(nullable = false)
    private boolean isMemoryEnabled;

    @Column(nullable = false)
    private boolean isMotherboardEnabled;

    @Column(nullable = false)
    private boolean isControllerEnabled;

    @Column(nullable = false)
    private boolean isNetworkEnabled;

    @Column(nullable = false)
    private boolean isStorageEnabled;

    @Column(nullable = false)
    private int collectEverySeconds;

    @Column
    private String apiKey;

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

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String toString() {
        return "AgentConfig(isCpuEnabled=" + this.isCpuEnabled() + ", isGpuEnabled=" + this.isGpuEnabled() + ", isMemoryEnabled=" + this.isMemoryEnabled() + ", isMotherboardEnabled=" + this.isMotherboardEnabled() + ", isControllerEnabled=" + this.isControllerEnabled() + ", isNetworkEnabled=" + this.isNetworkEnabled() + ", isStorageEnabled=" + this.isStorageEnabled() + ", collectEverySeconds=" + this.getCollectEverySeconds() + ", apiKey=" + this.getApiKey() + ")";
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof AgentConfig)) return false;
        final AgentConfig other = (AgentConfig) o;
        if (!other.canEqual((Object) this)) return false;
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
        if (this$apiKey == null ? other$apiKey != null : !this$apiKey.equals(other$apiKey)) return false;
        return true;
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
}
