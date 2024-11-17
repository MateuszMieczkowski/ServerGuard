package com.mmieczkowski.serverguard.dashboard;

import com.mmieczkowski.serverguard.dashboard.request.CreateDashboardRequest;
import com.mmieczkowski.serverguard.dashboard.request.GetGraphDataRequest;
import com.mmieczkowski.serverguard.dashboard.response.CreateDashboardResponse;
import com.mmieczkowski.serverguard.dashboard.response.GetDashboardResponse;
import com.mmieczkowski.serverguard.dashboard.response.GetDashboardsResponse;
import com.mmieczkowski.serverguard.dashboard.response.GetGraphDataResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/resourceGroups/{resourceGroupId}/agents/{agentId}/dashboards")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/{dashboardId}")
    public GetDashboardResponse getDashboard(@PathVariable UUID resourceGroupId,
                                             @PathVariable UUID agentId,
                                             @PathVariable UUID dashboardId) {
        return dashboardService.getDashboard(resourceGroupId, agentId, dashboardId);
    }

    @PostMapping
    public CreateDashboardResponse createDashboard(@PathVariable UUID resourceGroupId,
                                                   @PathVariable UUID agentId,
                                                   @Validated @RequestBody CreateDashboardRequest request) {
        return dashboardService.createDashboard(resourceGroupId, agentId, request);
    }

    @GetMapping
    public GetDashboardsResponse getDashboards(@PathVariable UUID resourceGroupId,
                                               @PathVariable UUID agentId) {
        return dashboardService.getDashboards(resourceGroupId, agentId);
    }

    @GetMapping("/{dashboardId}/graphs/{graphId}/data")
    public GetGraphDataResponse getGraphData(@PathVariable UUID resourceGroupId,
                                             @PathVariable UUID agentId,
                                             @PathVariable UUID dashboardId,
                                             @PathVariable int graphId,
                                             @Validated GetGraphDataRequest request) {
        return dashboardService.getGraphData(resourceGroupId, agentId, dashboardId, graphId, request);
    }

    @DeleteMapping("/{dashboardId}")
    public void deleteDashboard(@PathVariable UUID resourceGroupId,
                                @PathVariable UUID agentId,
                                @PathVariable UUID dashboardId) {
        dashboardService.deleteDashboard(resourceGroupId, agentId, dashboardId);
    }

    @PutMapping("/{dashboardId}")
    public void updateDashboard(@PathVariable UUID resourceGroupId,
                                @PathVariable UUID agentId,
                                @PathVariable UUID dashboardId,
                                @Validated @RequestBody CreateDashboardRequest request) {
        dashboardService.updateDashboard(resourceGroupId, agentId, dashboardId, request);
    }
}
