package com.mmieczkowski.serverguard.dashboard.model;

import com.mmieczkowski.serverguard.metric.model.MetricType;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Entity
public class Graph {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Getter
    private UUID id;

    @Column(nullable = false)
    @Getter
    private int index;

    @Column(nullable = false)
    @Getter
    private String sensorName;

    @Column(nullable = false)
    @Getter
    private String metricName;

    @Column(nullable = false)
    @Getter
    private MetricType metricType;

    @Column(nullable = false)
    @Getter
    private String lineColor;

    @ManyToOne(optional = false)
    private Dashboard dashboard;

    public Graph() {
    }

    public Graph(int index,
                 String sensorName,
                 String metricName,
                 MetricType metricType,
                 String lineColor) {
        this.index = index;
        this.sensorName = sensorName;
        this.metricName = metricName;
        this.metricType = metricType;
        this.lineColor = lineColor;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

}
