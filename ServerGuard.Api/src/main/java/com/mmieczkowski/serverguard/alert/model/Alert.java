package com.mmieczkowski.serverguard.alert.model;

import com.mmieczkowski.serverguard.agent.model.Agent;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Entity
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Getter
    private UUID id;

    @Column(nullable = false)
    @Getter
    private String name;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    @Getter
    private Agent agent;

    @Getter
    @Embedded
    private AlertMetric metric;

    @Embedded
    @Getter
    private AlertWhen when;

    @Embedded
    @Getter
    private GroupBy groupBy;

    @Getter
    @Column(nullable = false)
    private Duration duration;

    @Getter
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
        if(Float.isNaN(value)) {
            return false;
        }
        boolean triggered = when.evaluate(value);
        if(triggered) {
            resolvedAt = null;
        }
        if(!triggered && resolvedAt == null){
            resolvedAt = clock.instant();
        }
        return triggered;
    }

    public boolean shouldNotify(Clock clock) {
        if(resolvedAt != null) {
            return false;
        }
        if(nextNotificationAt == null) {
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
}
