package com.mmieczkowski.serverguard.agent.model;

import com.mmieczkowski.serverguard.dashboard.model.Dashboard;
import com.mmieczkowski.serverguard.resourcegroup.model.ResourceGroup;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.util.Assert;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
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

    @ManyToOne
    private ResourceGroup resourceGroup;

    @Getter
    @Embedded
    @Column(nullable = false, columnDefinition = "jsonb")
    private AgentConfig agentConfig;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @OneToMany(mappedBy = "agent", fetch = FetchType.LAZY)
    private List<Dashboard> dashboards;

    @Getter
    @Column(nullable = true)
    private Instant lastContactAt;

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

    public void setName(String name) {
        Assert.notNull(name, "Name cannot be null");
        this.name = name;
    }

    public void setLastContactAt(Instant lastContactAt) {
        Assert.notNull(lastContactAt, "Last contact at cannot be null");
        if(lastContactAt.isAfter(lastContactAt)) {
            return;
        }
        this.lastContactAt = lastContactAt;
    }

}
