package com.mmieczkowski.serverguard.alert.model;

import com.mmieczkowski.serverguard.agent.model.Agent;
import jakarta.persistence.*;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Entity
public class AlertLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    private Agent agent;

    @ManyToOne
    private Alert alert;

    @Embedded
    private AlertMetric metric;

    @Embedded
    private AlertWhen when;

    @Embedded
    private GroupBy groupBy;

    @Column(nullable = false)
    private Duration duration;

    @Column(nullable = false)
    private String AlertName;

    @Column(nullable = false)
    private Instant triggeredAt;

    @Column(nullable = false)
    private float triggeredByValue;


    public AlertLog() {
    }

    public AlertLog(Alert alert, float triggeredByValue, Clock clock) {
        this.alert = alert;
        this.agent = alert.getAgent();
        this.metric = alert.getMetric();
        this.when = alert.getWhen();
        this.groupBy = alert.getGroupBy();
        this.duration = alert.getDuration();
        this.AlertName = alert.getName();
        this.triggeredAt = clock.instant();
        this.triggeredByValue = triggeredByValue;
    }


    public UUID getId() {
        return this.id;
    }

    public Agent getAgent() {
        return this.agent;
    }

    public Alert getAlert() {
        return this.alert;
    }

    public AlertMetric getMetric() {
        return this.metric;
    }

    public AlertWhen getWhen() {
        return this.when;
    }

    public GroupBy getGroupBy() {
        return this.groupBy;
    }

    public Duration getDuration() {
        return this.duration;
    }

    public String getAlertName() {
        return this.AlertName;
    }

    public Instant getTriggeredAt() {
        return this.triggeredAt;
    }

    public float getTriggeredByValue() {
        return this.triggeredByValue;
    }
}
