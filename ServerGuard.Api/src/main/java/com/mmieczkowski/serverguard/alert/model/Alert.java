package com.mmieczkowski.serverguard.alert.model;

import com.mmieczkowski.serverguard.agent.model.Agent;
import jakarta.persistence.*;

import java.time.Clock;
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

    @Embedded
    private AlertMetric metric;

    @Embedded
    private AlertWhen when;

    @Embedded
    private GroupBy groupBy;

    @Column(nullable = false)
    private Duration duration;

    @Column()
    private Instant nextCheckAt;

    @Column()
    private Instant resolvedAt;

    @Column()
    private Instant nextNotificationAt;

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

    public boolean check(float value, Clock clock) {
        if (Float.isNaN(value)) {
            return false;
        }
        boolean triggered = when.evaluate(value);
        if (triggered) {
            resolvedAt = null;
        }
        if (!triggered && resolvedAt == null) {
            resolvedAt = clock.instant();
        }
        return triggered;
    }

    public boolean shouldNotify(Clock clock) {
        if (resolvedAt != null) {
            return false;
        }
        if (nextNotificationAt == null) {
            return true;
        }
        return clock.instant().isAfter(nextNotificationAt);
    }

    public void setNextNotification(Clock clock) {
        nextNotificationAt = clock.instant().plus(duration);
    }

    public void setNextCheck(Clock clock) {
        nextCheckAt = Instant.now(clock).plus(Duration.ofMinutes(1));
    }

    public UUID getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Agent getAgent() {
        return this.agent;
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
}
