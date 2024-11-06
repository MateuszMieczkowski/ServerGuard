package com.mmieczkowski.serverguard.agent.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@Embeddable
@EqualsAndHashCode
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
}
