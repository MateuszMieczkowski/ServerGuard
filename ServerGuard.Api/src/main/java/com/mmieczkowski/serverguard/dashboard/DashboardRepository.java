package com.mmieczkowski.serverguard.dashboard;

import com.mmieczkowski.serverguard.dashboard.model.Dashboard;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface DashboardRepository extends JpaRepository<Dashboard, UUID> {
    List<Dashboard> findAllByAgentId(UUID agentId, Sort sort);

    @Override
    @Modifying
    @Query("UPDATE Dashboard d SET d.isDeleted = true WHERE d = :dashboard")
    void delete(@NotNull Dashboard dashboard);
}
