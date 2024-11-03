package com.mmieczkowski.serverguard.resourcegroup;

import com.mmieczkowski.serverguard.config.CacheConstants;
import com.mmieczkowski.serverguard.resourcegroup.model.ResourceGroup;
import com.mmieczkowski.serverguard.resourcegroup.model.ResourceGroupUserRole;
import com.mmieczkowski.serverguard.resourcegroup.model.UserResourceGroupPermission;
import com.mmieczkowski.serverguard.resourcegroup.request.CreateResourceGroupRequest;
import com.mmieczkowski.serverguard.resourcegroup.response.CreateResourceGroupResponse;
import com.mmieczkowski.serverguard.resourcegroup.exception.ResourceGroupNotFoundException;
import com.mmieczkowski.serverguard.resourcegroup.response.GetResourceGroupResponse;
import com.mmieczkowski.serverguard.resourcegroup.request.GetResourceGroupPaginatedRequest;
import com.mmieczkowski.serverguard.resourcegroup.response.GetResourceGroupPaginatedResponse;
import com.mmieczkowski.serverguard.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ResourceGroupService {
    private final ResourceGroupRepository resourceGroupRepository;
    private final UserService userService;
    private final UserResourceGroupPermissionRepository userResourceGroupPermissionRepository;

    public ResourceGroupService(ResourceGroupRepository resourceGroupRepository, UserService userService, UserResourceGroupPermissionRepository userResourceGroupPermissionRepository) {
        this.resourceGroupRepository = resourceGroupRepository;
        this.userService = userService;
        this.userResourceGroupPermissionRepository = userResourceGroupPermissionRepository;
    }

    @Transactional
    public CreateResourceGroupResponse createResourceGroup(CreateResourceGroupRequest request) {
        var user = userService.getLoggedInUser()
                .orElseThrow();
        ResourceGroup resourceGroup = new ResourceGroup(request.name());
        resourceGroupRepository.save(resourceGroup);
        UserResourceGroupPermission userResourceGroupPermission = new UserResourceGroupPermission(resourceGroup,
                user,
                ResourceGroupUserRole.Admin);
        userResourceGroupPermissionRepository.save(userResourceGroupPermission);
        return new CreateResourceGroupResponse(resourceGroup.getId(), resourceGroup.getName());
    }

    public GetResourceGroupResponse getResourceGroup(UUID id){
        ResourceGroup resourceGroup = resourceGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceGroupNotFoundException(id));
        return new GetResourceGroupResponse(resourceGroup.getId(), resourceGroup.getName());
    }

    public GetResourceGroupPaginatedResponse getResourceGroups(GetResourceGroupPaginatedRequest request,
                                                               Authentication authentication) {
        var user = userService.getLoggedInUser()
                .orElseThrow();
        var pageable = PageRequest.of(request.pageNumber(), request.pageSize())
                .withSort(Sort.by("rg.name"));
        Page<ResourceGroup> page = resourceGroupRepository.findByUserId(user.getId(), pageable);
        var items = page.get()
                .map(GetResourceGroupPaginatedResponse.ResourceGroup::new)
                .toList();
        return new GetResourceGroupPaginatedResponse(items, page.getNumber(), page.getSize(), page.getTotalPages());
    }
}
