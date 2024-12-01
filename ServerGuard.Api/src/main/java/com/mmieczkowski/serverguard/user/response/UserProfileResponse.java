package com.mmieczkowski.serverguard.user.response;

import com.mmieczkowski.serverguard.resourcegroup.model.ResourceGroupUserRole;

import java.util.List;
import java.util.UUID;

public record UserProfileResponse(UUID id, String email, List<ResourceGroupPermission> resourceGroupPermissions) {
    public record ResourceGroupPermission(UUID id, ResourceGroupUserRole role) {
    }
}
