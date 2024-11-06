package com.mmieczkowski.serverguard.dashboard.model;

import com.mmieczkowski.serverguard.agent.model.Agent;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.util.Assert;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Entity
@SQLRestriction("is_deleted = false")
public class Dashboard {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Getter
    private UUID id;

    @Column(nullable = false)
    @Getter
    private String name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "dashboard")
    private List<Graph> graphs;

    @ManyToOne
    private Agent agent;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private final boolean isDeleted = false;

    public Dashboard() {
    }

    public Dashboard(String name, List<Graph> graphs, Agent agent) {
        Assert.notNull(name, "Name cannot be null");
        Assert.notNull(graphs, "Graphs cannot be null");
        Assert.notEmpty(graphs, "Graphs cannot be empty");
        this.name = name;
        this.graphs = graphs;
        this.agent = agent;
    }

    public Stream<Graph> getGraphs() {
        return graphs.stream();
    }

    public void setName(String name) {
        Assert.notNull(name, "Name cannot be null");
        Assert.hasText(name, "Name cannot be empty");
        this.name = name;
    }

    public void clearGraphs() {
        graphs.clear();
    }

    public void addGraph(Graph graph) {
        Assert.notNull(graph, "Graph cannot be null");
        graph.setDashboard(this);
        graphs.add(graph);
    }

}
