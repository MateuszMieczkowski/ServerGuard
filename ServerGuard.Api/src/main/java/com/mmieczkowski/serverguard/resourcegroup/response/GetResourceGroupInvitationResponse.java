package com.mmieczkowski.serverguard.resourcegroup.response;

import com.mmieczkowski.serverguard.resourcegroup.model.ResourceGroupUserRole;

public record GetResourceGroupInvitationResponse(String email, ResourceGroupUserRole role, String resourceGroupName) {
}
