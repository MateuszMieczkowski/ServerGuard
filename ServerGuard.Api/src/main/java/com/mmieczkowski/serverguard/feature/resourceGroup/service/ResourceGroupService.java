package com.mmieczkowski.serverguard.feature.resourceGroup.service;

import com.mmieczkowski.serverguard.config.CacheConstants;
import com.mmieczkowski.serverguard.feature.resourceGroup.create.CreateResourceGroupRequest;
import com.mmieczkowski.serverguard.feature.resourceGroup.create.CreateResourceGroupResponse;
import com.mmieczkowski.serverguard.feature.resourceGroup.exception.ResourceGroupNotFoundException;
import com.mmieczkowski.serverguard.feature.resourceGroup.get.GetResourceGroupResponse;
import com.mmieczkowski.serverguard.feature.resourceGroup.getPaginated.GetResourceGroupPaginatedRequest;
import com.mmieczkowski.serverguard.feature.resourceGroup.getPaginated.GetResourceGroupPaginatedResponse;
import com.mmieczkowski.serverguard.model.ResourceGroup;
import com.mmieczkowski.serverguard.model.ResourceGroupUserRole;
import com.mmieczkowski.serverguard.model.UserResourceGroupPermission;
import com.mmieczkowski.serverguard.repository.ResourceGroupRepository;
import com.mmieczkowski.serverguard.repository.UserResourceGroupPermissionRepository;
import com.mmieczkowski.serverguard.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    @CacheEvict(value = CacheConstants.ResourceGroupCache, allEntries = true)
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

    @Cacheable(CacheConstants.ResourceGroupCache)
    public GetResourceGroupResponse getResourceGroup(UUID id){
        ResourceGroup resourceGroup = resourceGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceGroupNotFoundException(id));
        return new GetResourceGroupResponse(resourceGroup.getId(), resourceGroup.getName());
    }

    @Cacheable(value = CacheConstants.ResourceGroupCache, key = "{#request.pageNumber(), #request.pageSize(), #authentication.name}")
    public GetResourceGroupPaginatedResponse getResourceGroups(GetResourceGroupPaginatedRequest request,
                                                               Authentication authentication) {
        var user = userService.getLoggedInUser()
                .orElseThrow();
        var pageable = PageRequest.of(request.pageNumber(), request.pageSize());
        Page<ResourceGroup> page = resourceGroupRepository.findByUserId(user.getId(), pageable);
        var items = page.get()
                .map(com.mmieczkowski.serverguard.feature.resourceGroup.getPaginated.ResourceGroup::new)
                .toList();
        return new GetResourceGroupPaginatedResponse(items, page.getNumber(), page.getSize(), page.getTotalPages());
    }
}
