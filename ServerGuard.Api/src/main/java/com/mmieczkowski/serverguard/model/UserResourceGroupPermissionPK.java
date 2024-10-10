package com.mmieczkowski.serverguard.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class UserResourceGroupPermissionPK implements Serializable {

    @Column(name = "app_user_id")
    private UUID userId;

    @Column(name = "resource_group_id")
    private UUID resourceGroupId;

    public UserResourceGroupPermissionPK(UUID userId, UUID resourceGroupId) {
        this.userId = userId;
        this.resourceGroupId = resourceGroupId;
    }

    public UserResourceGroupPermissionPK() {

    }
}
