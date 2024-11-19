package com.mmieczkowski.serverguard.dashboard;

import com.mmieczkowski.serverguard.agent.AgentRepository;
import com.mmieczkowski.serverguard.agent.exception.AgentNotFoundException;
import com.mmieczkowski.serverguard.agent.model.Agent;
import com.mmieczkowski.serverguard.annotation.ResourceGroupAccess;
import com.mmieczkowski.serverguard.dashboard.exception.DashboardNotFoundException;
import com.mmieczkowski.serverguard.dashboard.exception.GraphNotFoundException;
import com.mmieczkowski.serverguard.dashboard.model.Dashboard;
import com.mmieczkowski.serverguard.dashboard.model.Graph;
import com.mmieczkowski.serverguard.dashboard.request.CreateDashboardRequest;
import com.mmieczkowski.serverguard.dashboard.request.GetGraphDataRequest;
import com.mmieczkowski.serverguard.dashboard.response.CreateDashboardResponse;
import com.mmieczkowski.serverguard.dashboard.response.GetDashboardResponse;
import com.mmieczkowski.serverguard.dashboard.response.GetDashboardsResponse;
import com.mmieczkowski.serverguard.dashboard.response.GetGraphDataResponse;
import com.mmieczkowski.serverguard.metric.MetricRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DashboardService {
    private final AgentRepository agentRepository;
    private final DashboardRepository dashboardRepository;
    private final MetricRepository metricRepository;

    public DashboardService(AgentRepository agentRepository,
                            DashboardRepository dashboardRepository,
                            MetricRepository metricRepository) {
        this.agentRepository = agentRepository;
        this.dashboardRepository = dashboardRepository;
        this.metricRepository = metricRepository;
    }

    @Transactional
    @ResourceGroupAccess
    public CreateDashboardResponse createDashboard(UUID resourceGroupId, UUID agentId,
                                                   CreateDashboardRequest request) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(AgentNotFoundException::new);

        List<Graph> graphs = request.graphs()
                .stream()
                .map(graph -> new Graph(graph.index(), graph.sensorName(), graph.metricName(),
                        graph.metricType(), graph.lineColor()))
                .toList();
        Dashboard dashboard = new Dashboard(request.name(), graphs, agent);
        for (Graph graph : graphs) {
            graph.setDashboard(dashboard);
        }
        dashboardRepository.saveAndFlush(dashboard);
        return new CreateDashboardResponse(dashboard.getId(), dashboard.getName());
    }

    @ResourceGroupAccess
    public GetDashboardResponse getDashboard(UUID resourceGroupId, UUID agentId, UUID dashboardId) {
        agentRepository.findById(agentId)
                .orElseThrow(AgentNotFoundException::new);
        Dashboard dashboard = dashboardRepository.findById(dashboardId)
                .orElseThrow(DashboardNotFoundException::new);

        List<GetDashboardResponse.Graph> graphs = dashboard.getGraphs()
                .map(graph -> new GetDashboardResponse.Graph(graph.getIndex(),
                        graph.getSensorName(),
                        graph.getMetricName(),
                        graph.getMetricType(),
                        graph.getLineColor(),
                        graph.getMetricType().getUnit()))
                .toList();

        return new GetDashboardResponse(dashboard.getName(), graphs);
    }

    @ResourceGroupAccess
    public GetDashboardsResponse getDashboards(UUID resourceGroupId, UUID agentId) {
        agentRepository.findById(agentId)
                .orElseThrow(AgentNotFoundException::new);
        var dashboards = dashboardRepository.findAllByAgentId(agentId, Sort.by("name")).stream()
                .map(dashboard -> new GetDashboardsResponse.Dashboard(dashboard.getId(),
                        dashboard.getName()))
                .toList();

        return new GetDashboardsResponse(dashboards);
    }

    @ResourceGroupAccess
    public GetGraphDataResponse getGraphData(UUID resourceGroupId, UUID agentId, UUID dashboardId, int graphIndex,
                                             GetGraphDataRequest request) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(AgentNotFoundException::new);
        Dashboard dashboard = dashboardRepository.findById(dashboardId)
                .orElseThrow(DashboardNotFoundException::new);
        var graph = dashboard.getGraphs()
                .filter(g -> g.getIndex() == graphIndex)
                .findFirst()
                .orElseThrow(GraphNotFoundException::new);
        var dataFrom = LocalDateTime.ofInstant(request.from(), java.time.ZoneOffset.UTC);
        var dataTo = LocalDateTime.ofInstant(request.to(), java.time.ZoneOffset.UTC);
        if (request.aggregationType().equals("LTTB")) {
            var dataPoints = metricRepository.findLttbMetricsByAgentId(agent.getId(),
                    graph.getSensorName(),
                    graph.getMetricName(),
                    graph.getMetricType(),
                    dataFrom,
                    dataTo,
                    request.maxDataPoints());
            return new GetGraphDataResponse(dataPoints);
        } else {
            int totalMinutes = (int) request.from().until(request.to(),
                    java.time.temporal.ChronoUnit.MINUTES);
            int intervalMinutes = Math.ceilDiv(totalMinutes, request.maxDataPoints());
            var dataPoints = metricRepository.findAvgMetricsByAgentId(agent.getId(),
                    graph.getSensorName(),
                    graph.getMetricName(),
                    graph.getMetricType(),
                    dataFrom,
                    dataTo,
                    intervalMinutes);
            return new GetGraphDataResponse(dataPoints);
        }
    }

    @ResourceGroupAccess
    public void deleteDashboard(UUID resourceGroupId, UUID agentId, UUID dashboardId) {
        agentRepository.findById(agentId)
                .orElseThrow(AgentNotFoundException::new);
        var dashboardOptional = dashboardRepository.findById(dashboardId);
        if (dashboardOptional.isEmpty()) {
            return;
        }
        dashboardRepository.delete(dashboardOptional.get());
    }

    @ResourceGroupAccess
    public void updateDashboard(UUID resourceGroupId, UUID agentId, UUID dashboardId,
                                CreateDashboardRequest request) {
        agentRepository.findById(agentId)
                .orElseThrow(AgentNotFoundException::new);
        Dashboard dashboard = dashboardRepository.findById(dashboardId)
                .orElseThrow(DashboardNotFoundException::new);
        dashboard.setName(request.name());
        dashboard.clearGraphs();
        for (var graph : request.graphs()) {
            dashboard.addGraph(new Graph(graph.index(), graph.sensorName(), graph.metricName(),
                    graph.metricType(), graph.lineColor()));
        }
        dashboardRepository.saveAndFlush(dashboard);
    }
}
