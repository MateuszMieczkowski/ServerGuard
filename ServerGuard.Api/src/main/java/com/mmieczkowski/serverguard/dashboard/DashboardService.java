package com.mmieczkowski.serverguard.dashboard;

import com.mmieczkowski.serverguard.agent.exception.AgentNotFoundException;
import com.mmieczkowski.serverguard.dashboard.exception.GraphNotFoundException;
import com.mmieczkowski.serverguard.dashboard.model.Dashboard;
import com.mmieczkowski.serverguard.dashboard.model.Graph;
import com.mmieczkowski.serverguard.dashboard.request.CreateDashboardRequest;
import com.mmieczkowski.serverguard.dashboard.request.GetGraphDataRequest;
import com.mmieczkowski.serverguard.dashboard.response.CreateDashboardResponse;
import com.mmieczkowski.serverguard.dashboard.exception.DashboardNotFoundException;
import com.mmieczkowski.serverguard.dashboard.response.GetDashboardResponse;
import com.mmieczkowski.serverguard.dashboard.response.GetDashboardsResponse;
import com.mmieczkowski.serverguard.dashboard.response.GetGraphDataResponse;
import com.mmieczkowski.serverguard.metric.MetricRepository;
import com.mmieczkowski.serverguard.resourcegroup.exception.ResourceGroupNotFoundException;
import com.mmieczkowski.serverguard.agent.model.Agent;
import com.mmieczkowski.serverguard.agent.AgentRepository;
import com.mmieczkowski.serverguard.service.UserService;
import com.mmieczkowski.serverguard.user.User;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class DashboardService {
        private final AgentRepository agentRepository;
        private final UserService userService;
        private final DashboardRepository dashboardRepository;
        private final MetricRepository metricRepository;

        @Transactional
        public CreateDashboardResponse createDashboard(UUID resourceGroupId, UUID agentId,
                        CreateDashboardRequest request) {
                var userId = userService.getLoggedInUser()
                                .orElseThrow()
                                .getId();
                Agent agent = agentRepository.findAgentByIdAndUserId(agentId, userId)
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

        public GetDashboardResponse getDashboard(UUID resourceGroupId, UUID agentId, UUID dashboardId) {
                var user = getUserOrThrow(resourceGroupId);
                agentRepository.findAgentByIdAndUserId(agentId, user.getId())
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

        public GetDashboardsResponse getDashboards(UUID resourceGroupId, UUID agentId) {
                var user = getUserOrThrow(resourceGroupId);
                agentRepository.findAgentByIdAndUserId(agentId, user.getId())
                                .orElseThrow(AgentNotFoundException::new);
                var dashboards = dashboardRepository.findAllByAgentId(agentId, Sort.by("name")).stream()
                                .map(dashboard -> new GetDashboardsResponse.Dashboard(dashboard.getId(),
                                                dashboard.getName()))
                                .toList();

                return new GetDashboardsResponse(dashboards);
        }

        public GetGraphDataResponse getGraphData(UUID resourceGroupId, UUID agentId, UUID dashboardId, int graphIndex,
                        GetGraphDataRequest request) {
                var user = getUserOrThrow(resourceGroupId);
                Agent agent = agentRepository.findAgentByIdAndUserId(agentId, user.getId())
                                .orElseThrow(AgentNotFoundException::new);
                Dashboard dashboard = dashboardRepository.findById(dashboardId)
                                .orElseThrow(DashboardNotFoundException::new);
                var graph = dashboard.getGraphs()
                                .filter(g -> g.getIndex() == graphIndex)
                                .findFirst()
                                .orElseThrow(GraphNotFoundException::new);

                if (request.aggregationType().equals("LTTB")) {
                        var dataPoints = metricRepository.findLttbMetricsByAgentId(agent.getId(),
                                        graph.getSensorName(),
                                        graph.getMetricName(),
                                        graph.getMetricType(),
                                        request.from(),
                                        request.to(),
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
                                        request.from(),
                                        request.to(),
                                        intervalMinutes);
                        return new GetGraphDataResponse(dataPoints);
                }
        }

        @NotNull
        private User getUserOrThrow(UUID resourceGroupId) {
                var user = userService.getLoggedInUser()
                                .orElseThrow();
                if (!user.hasAccessToResourceGroup(resourceGroupId)) {
                        throw new ResourceGroupNotFoundException(resourceGroupId);
                }
                return user;
        }

        public void deleteDashboard(UUID resourceGroupId, UUID agentId, UUID dashboardId) {
                var user = getUserOrThrow(resourceGroupId);
                agentRepository.findAgentByIdAndUserId(agentId, user.getId())
                                .orElseThrow(AgentNotFoundException::new);
                var dashboardOptional = dashboardRepository.findById(dashboardId);
                if (dashboardOptional.isEmpty()) {
                        return;
                }
                dashboardRepository.delete(dashboardOptional.get());
        }

        public void updateDashboard(UUID resourceGroupId, UUID agentId, UUID dashboardId,
                        CreateDashboardRequest request) {
                var user = getUserOrThrow(resourceGroupId);
                agentRepository.findAgentByIdAndUserId(agentId, user.getId())
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
