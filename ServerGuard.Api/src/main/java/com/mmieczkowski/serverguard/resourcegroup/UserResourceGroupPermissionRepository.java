package com.mmieczkowski.serverguard.resourcegroup;

import com.mmieczkowski.serverguard.resourcegroup.model.UserResourceGroupPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserResourceGroupPermissionRepository extends JpaRepository<UserResourceGroupPermission, UUID> {

    Optional<UserResourceGroupPermission> findByUserIdAndResourceGroupId(UUID id, UUID resourceGroupId);
}
