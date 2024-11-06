package com.mmieczkowski.serverguard.alert.model;

import com.mmieczkowski.serverguard.agent.model.Agent;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Entity
public class AlertLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Getter
    private UUID id;

    @ManyToOne(optional = false)
    @Getter
    private Agent agent;

    @ManyToOne
    @Getter
    private Alert alert;

    @Embedded
    @Getter
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

    @Column(nullable = false)
    @Getter
    private String AlertName;

    @Column(nullable = false)
    @Getter
    private Instant triggeredAt;

    @Column(nullable = false)
    @Getter
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


}
