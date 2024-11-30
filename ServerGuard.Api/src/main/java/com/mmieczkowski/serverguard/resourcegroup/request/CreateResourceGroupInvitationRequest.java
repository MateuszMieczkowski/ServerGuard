package com.mmieczkowski.serverguard.resourcegroup.request;

import com.mmieczkowski.serverguard.resourcegroup.model.ResourceGroupUserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record CreateResourceGroupInvitationRequest(@Email @Size(max=50) String email, ResourceGroupUserRole role) {
}
