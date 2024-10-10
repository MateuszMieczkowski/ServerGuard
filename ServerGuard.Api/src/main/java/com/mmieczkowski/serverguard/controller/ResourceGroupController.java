package com.mmieczkowski.serverguard.controller;

import com.mmieczkowski.serverguard.feature.resourceGroup.create.CreateResourceGroupRequest;
import com.mmieczkowski.serverguard.feature.resourceGroup.create.CreateResourceGroupResponse;
import com.mmieczkowski.serverguard.feature.resourceGroup.get.GetResourceGroupResponse;
import com.mmieczkowski.serverguard.feature.resourceGroup.getPaginated.GetResourceGroupPaginatedRequest;
import com.mmieczkowski.serverguard.feature.resourceGroup.getPaginated.GetResourceGroupPaginatedResponse;
import com.mmieczkowski.serverguard.feature.resourceGroup.service.ResourceGroupService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/resourceGroups")
public class ResourceGroupController {

    private final ResourceGroupService resourceGroupService;

    public ResourceGroupController(ResourceGroupService resourceGroupService) {
        this.resourceGroupService = resourceGroupService;
    }

    @PostMapping
    public CreateResourceGroupResponse createResourceGroup(
            @RequestBody CreateResourceGroupRequest request) {
        return resourceGroupService.createResourceGroup(request);
    }

    @GetMapping("{id}")
    public GetResourceGroupResponse getResourceGroup(@PathVariable UUID id) {
        return resourceGroupService.getResourceGroup(id);
    }

    @GetMapping()
    public GetResourceGroupPaginatedResponse getResourceGroupsPaginated(GetResourceGroupPaginatedRequest request, Authentication authentication) {
        return resourceGroupService.getResourceGroups(request, authentication);
    }
}
