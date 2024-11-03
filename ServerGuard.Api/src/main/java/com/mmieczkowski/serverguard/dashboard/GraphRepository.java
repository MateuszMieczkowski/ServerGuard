package com.mmieczkowski.serverguard.dashboard;

import com.mmieczkowski.serverguard.dashboard.model.Graph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GraphRepository extends JpaRepository<Graph, UUID> {
}
