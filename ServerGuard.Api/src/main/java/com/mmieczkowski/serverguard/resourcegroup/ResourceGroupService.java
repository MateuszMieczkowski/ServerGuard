package com.mmieczkowski.serverguard.resourcegroup;

import com.mmieczkowski.serverguard.annotation.ResourceGroupAccess;
import com.mmieczkowski.serverguard.resourcegroup.model.ResourceGroup;
import com.mmieczkowski.serverguard.resourcegroup.model.ResourceGroupUserRole;
import com.mmieczkowski.serverguard.resourcegroup.model.UserResourceGroupPermission;
import com.mmieczkowski.serverguard.resourcegroup.request.CreateResourceGroupRequest;
import com.mmieczkowski.serverguard.resourcegroup.response.CreateResourceGroupResponse;
import com.mmieczkowski.serverguard.resourcegroup.exception.ResourceGroupNotFoundException;
import com.mmieczkowski.serverguard.resourcegroup.response.GetResourceGroupResponse;
import com.mmieczkowski.serverguard.resourcegroup.request.GetResourceGroupPaginatedRequest;
import com.mmieczkowski.serverguard.resourcegroup.response.GetResourceGroupPaginatedResponse;
import com.mmieczkowski.serverguard.service.CurrentUserService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ResourceGroupService {
    private final ResourceGroupRepository resourceGroupRepository;
    private final CurrentUserService currentUserService;
    private final UserResourceGroupPermissionRepository userResourceGroupPermissionRepository;

    public ResourceGroupService(ResourceGroupRepository resourceGroupRepository, CurrentUserService currentUserService, UserResourceGroupPermissionRepository userResourceGroupPermissionRepository) {
        this.resourceGroupRepository = resourceGroupRepository;
        this.currentUserService = currentUserService;
        this.userResourceGroupPermissionRepository = userResourceGroupPermissionRepository;
    }

    @Transactional
    public CreateResourceGroupResponse createResourceGroup(CreateResourceGroupRequest request) {
        var user = currentUserService.getLoggedInUser()
                .orElseThrow();
        ResourceGroup resourceGroup = new ResourceGroup(request.name());
        resourceGroupRepository.save(resourceGroup);
        UserResourceGroupPermission userResourceGroupPermission = new UserResourceGroupPermission(resourceGroup,
                user,
                ResourceGroupUserRole.Admin);
        userResourceGroupPermissionRepository.save(userResourceGroupPermission);
        return new CreateResourceGroupResponse(resourceGroup.getId(), resourceGroup.getName());
    }

    @ResourceGroupAccess("id")
    public GetResourceGroupResponse getResourceGroup(UUID id){
        ResourceGroup resourceGroup = resourceGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceGroupNotFoundException(id));
        return new GetResourceGroupResponse(resourceGroup.getId(), resourceGroup.getName());
    }

    public GetResourceGroupPaginatedResponse getResourceGroups(GetResourceGroupPaginatedRequest request) {
        var user = currentUserService.getLoggedInUser()
                .orElseThrow();
        var pageable = PageRequest.of(request.pageNumber(), request.pageSize())
                .withSort(Sort.by("rg.name"));
        Page<ResourceGroup> page = resourceGroupRepository.findByUserId(user.getId(), pageable);
        var items = page.get()
                .map(GetResourceGroupPaginatedResponse.ResourceGroup::new)
                .toList();
        return new GetResourceGroupPaginatedResponse(items, page.getNumber(), page.getSize(), page.getTotalPages());
    }

    @ResourceGroupAccess("id")
    public void deleteResourceGroup(UUID id) {
        resourceGroupRepository.deleteById(id);
    }

    @ResourceGroupAccess("id")
    public void updateResourceGroup(UUID id, CreateResourceGroupRequest request) {
        ResourceGroup resourceGroup = resourceGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceGroupNotFoundException(id));
        resourceGroup.setName(request.name());
        resourceGroupRepository.save(resourceGroup);
    }
}
