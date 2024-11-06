package com.mmieczkowski.serverguard.alert;

import com.mmieczkowski.serverguard.alert.model.AlertLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AlertLogRepository extends JpaRepository<AlertLog, UUID> {

    Page<AlertLog> findAllByAgentId(UUID agentId, Pageable pageable);
}
