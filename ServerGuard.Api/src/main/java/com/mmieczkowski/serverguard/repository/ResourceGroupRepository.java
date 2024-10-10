package com.mmieczkowski.serverguard.repository;

import com.mmieczkowski.serverguard.model.ResourceGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface ResourceGroupRepository extends JpaRepository<ResourceGroup, UUID>{
    @Query("SELECT rg FROM UserResourceGroupPermission up JOIN ResourceGroup rg ON up.resourceGroup.id = rg.id")
    Page<ResourceGroup> findByUserId(UUID userId, Pageable pageable);
}
