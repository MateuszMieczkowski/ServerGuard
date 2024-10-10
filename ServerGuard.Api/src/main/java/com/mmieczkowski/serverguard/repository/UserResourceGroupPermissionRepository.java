package com.mmieczkowski.serverguard.repository;

import com.mmieczkowski.serverguard.model.UserResourceGroupPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserResourceGroupPermissionRepository extends JpaRepository<UserResourceGroupPermission, UUID> {

}
