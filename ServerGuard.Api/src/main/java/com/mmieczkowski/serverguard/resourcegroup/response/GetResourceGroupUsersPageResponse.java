package com.mmieczkowski.serverguard.resourcegroup.response;

import com.mmieczkowski.serverguard.resourcegroup.model.ResourceGroupUserRole;
import org.springframework.data.domain.Page;

import java.util.UUID;

public record GetResourceGroupUsersPageResponse(Page<User> users) {
    public record User(UUID id, String email, ResourceGroupUserRole role) {
    }
}
