package com.mmieczkowski.serverguard.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
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

    @Column(nullable = false, columnDefinition = "integer default 15")
    private int collectEverySeconds;

    @Column(nullable = true)
    private String apiKey;
}
