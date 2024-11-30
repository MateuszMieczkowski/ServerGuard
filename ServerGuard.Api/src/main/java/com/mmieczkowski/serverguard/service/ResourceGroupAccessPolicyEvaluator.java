package com.mmieczkowski.serverguard.service;

import com.mmieczkowski.serverguard.resourcegroup.UserResourceGroupPermissionRepository;
import com.mmieczkowski.serverguard.resourcegroup.model.ResourceGroupUserRole;
import com.mmieczkowski.serverguard.user.model.User;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class ResourceGroupAccessPolicyEvaluator {

    private final UserResourceGroupPermissionRepository userResourceGroupPermissionRepository;
    private final CurrentUserService currentUserService;

    public ResourceGroupAccessPolicyEvaluator(UserResourceGroupPermissionRepository userResourceGroupPermissionRepository, CurrentUserService currentUserService) {
        this.userResourceGroupPermissionRepository = userResourceGroupPermissionRepository;
        this.currentUserService = currentUserService;
    }

    public boolean evaluate(UUID resourceGroupId, ResourceGroupUserRole[] roles) throws Throwable {
        User user = currentUserService.getLoggedInUser().orElseThrow((Supplier<Throwable>) () -> new AuthenticationCredentialsNotFoundException("User not logged in"));
        var optionalUserResourceGroupPermission = userResourceGroupPermissionRepository.findByUserIdAndResourceGroupId(user.getId(), resourceGroupId);
        return optionalUserResourceGroupPermission
                .filter(userResourceGroupPermission -> Arrays.stream(roles).anyMatch(role -> role.equals(userResourceGroupPermission.getRole())))
                .isPresent();
    }
}
