package com.mmieczkowski.serverguard.resourcegroup.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
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

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof UserResourceGroupPermissionPK other)) return false;
        if (!other.canEqual(this)) return false;
        final Object this$userId = this.userId;
        final Object other$userId = other.userId;
        if (!Objects.equals(this$userId, other$userId)) return false;
        final Object this$resourceGroupId = this.resourceGroupId;
        final Object other$resourceGroupId = other.resourceGroupId;
        return Objects.equals(this$resourceGroupId, other$resourceGroupId);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof UserResourceGroupPermissionPK;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $userId = this.userId;
        result = result * PRIME + ($userId == null ? 43 : $userId.hashCode());
        final Object $resourceGroupId = this.resourceGroupId;
        result = result * PRIME + ($resourceGroupId == null ? 43 : $resourceGroupId.hashCode());
        return result;
    }

    public UUID getResourceGroupId() {
        return this.resourceGroupId;
    }
}
