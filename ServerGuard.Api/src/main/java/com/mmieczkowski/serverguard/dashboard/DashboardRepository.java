package com.mmieczkowski.serverguard.dashboard;

import com.mmieczkowski.serverguard.dashboard.model.Dashboard;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DashboardRepository extends JpaRepository<Dashboard, UUID> {
    List<Dashboard> findAllByAgentId(UUID agentId, Sort sort);
}
