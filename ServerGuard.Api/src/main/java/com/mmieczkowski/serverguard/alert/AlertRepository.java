package com.mmieczkowski.serverguard.alert;

import com.mmieczkowski.serverguard.alert.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AlertRepository extends JpaRepository<Alert, UUID> {
}
