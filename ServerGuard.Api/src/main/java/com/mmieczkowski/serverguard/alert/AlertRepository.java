package com.mmieczkowski.serverguard.alert;

import com.mmieczkowski.serverguard.alert.model.Alert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface AlertRepository extends JpaRepository<Alert, UUID> {
    @Query("SELECT a FROM Alert a WHERE a.nextCheckAt IS NULL OR a.nextCheckAt <= CURRENT_TIMESTAMP")
    List<Alert> findAllWhereNextCheckIsNullOrPast();

    Page<Alert> findAllByAgentId(UUID id, Pageable pageable);
}
