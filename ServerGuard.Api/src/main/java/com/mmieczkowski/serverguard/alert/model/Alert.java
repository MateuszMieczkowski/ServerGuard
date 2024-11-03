package com.mmieczkowski.serverguard.alert.model;

import com.mmieczkowski.serverguard.agent.model.Agent;
import com.mmieczkowski.serverguard.alert.model.groupby.GroupBy;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Entity
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Agent agent;

    @Getter
    @Embedded
    private AlertMetric metric;

    @Embedded
    private AlertWhen when;

    @Embedded
    private GroupBy groupBy;

    @Getter
    @Column(nullable = false)
    private Duration duration;

    @Getter
    @Setter
    @Column(nullable = true
    )
    private Instant nextCheck;

    public Alert(String name, Agent agent, AlertMetric metric, AlertWhen when, GroupBy groupBy, Duration duration) {
        this.name = name;
        this.agent = agent;
        this.metric = metric;
        this.when = when;
        this.groupBy = groupBy;
        this.duration = duration;
    }

    public Alert() {

    }

    public boolean check(float value) {
        return when.evaluate(value);
    }
}
