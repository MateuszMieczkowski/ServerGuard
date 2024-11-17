package com.mmieczkowski.serverguard.dashboard.model;

import com.mmieczkowski.serverguard.metric.model.MetricType;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Graph {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private int index;

    @Column(nullable = false)
    private String sensorName;

    @Column(nullable = false)
    private String metricName;

    @Column(nullable = false)
    private MetricType metricType;

    @Column(nullable = false)
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

    public UUID getId() {
        return this.id;
    }

    public int getIndex() {
        return this.index;
    }

    public String getSensorName() {
        return this.sensorName;
    }

    public String getMetricName() {
        return this.metricName;
    }

    public MetricType getMetricType() {
        return this.metricType;
    }

    public String getLineColor() {
        return this.lineColor;
    }
}
