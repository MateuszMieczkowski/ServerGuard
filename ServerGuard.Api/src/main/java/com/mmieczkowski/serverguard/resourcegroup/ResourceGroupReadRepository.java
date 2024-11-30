package com.mmieczkowski.serverguard.resourcegroup;

import com.mmieczkowski.serverguard.resourcegroup.model.ResourceGroup;
import com.mmieczkowski.serverguard.resourcegroup.response.GetResourceGroupUsersPageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface ResourceGroupReadRepository extends JpaRepository<ResourceGroup, UUID> {

    @SuppressWarnings("JpaQlInspection")
    @Query("""
            SELECT new com.mmieczkowski.serverguard.resourcegroup.response.GetResourceGroupUsersPageResponse$User(u.id, u.email, urgp.role)
            FROM UserResourceGroupPermission urgp
                JOIN User u ON urgp.user.id = u.id
            WHERE urgp.resourceGroup.id = :resourceGroupId""")
    Page<GetResourceGroupUsersPageResponse.User> findUsersByResourceGroupId(UUID resourceGroupId, Pageable pageable);
}
