package com.mmieczkowski.serverguard.model;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.UUID;

@Entity
@SQLRestriction("is_deleted = false")
public class Agent {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Getter
    @Column(nullable = false)
    private String name;

    @CreatedDate
    private Instant createdDate;

    @LastModifiedDate
    private Instant lastModifiedDate;

    @ManyToOne
    private ResourceGroup resourceGroup;

    @Embedded
    @Column(nullable = false, columnDefinition = "jsonb")
    private AgentConfig agentConfig;

    @Column(nullable = false)
    private boolean isDeleted = false;

    public Agent(String name, ResourceGroup resourceGroup, AgentConfig agentConfig) {
        Assert.notNull(name, "Name cannot be null");
        Assert.notNull(resourceGroup, "Resource group cannot be null");
        Assert.notNull(agentConfig, "Agent config cannot be null");
        this.name = name;
        this.agentConfig = agentConfig;
        this.resourceGroup = resourceGroup;
    }

    public Agent() {

    }
}
