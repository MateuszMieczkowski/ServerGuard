package com.mmieczkowski.serverguard.dashboard.response;

import java.util.List;
import java.util.UUID;

public record GetDashboardsResponse(List<Dashboard> dashboards) {
    public record Dashboard(UUID id, String name) {
    }
}
