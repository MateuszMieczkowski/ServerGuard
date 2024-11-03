package com.mmieczkowski.serverguard.resourcegroup.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@EqualsAndHashCode
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
