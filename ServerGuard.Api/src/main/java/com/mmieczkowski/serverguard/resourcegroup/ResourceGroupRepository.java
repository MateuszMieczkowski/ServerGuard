package com.mmieczkowski.serverguard.resourcegroup;

import com.mmieczkowski.serverguard.resourcegroup.model.ResourceGroup;
import com.mmieczkowski.serverguard.resourcegroup.model.UserResourceGroupPermission;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ResourceGroupRepository extends JpaRepository<ResourceGroup, UUID>{
    @Query("""
            SELECT rg
            FROM UserResourceGroupPermission up
                JOIN ResourceGroup rg ON up.resourceGroup.id = rg.id AND up.user.id = :userId""")
    Page<ResourceGroup> findByUserId(UUID userId, Pageable pageable);

    @Query("""
            SELECT u.email
            FROM UserResourceGroupPermission up
                JOIN User u ON up.user.id = u.id AND up.resourceGroup.id = :resourceGroupId""")
    List<String> findAllResourceGroupUserEmails(UUID resourceGroupId);
}
