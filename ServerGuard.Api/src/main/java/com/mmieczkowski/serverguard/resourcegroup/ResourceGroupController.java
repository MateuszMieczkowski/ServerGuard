package com.mmieczkowski.serverguard.resourcegroup;

import com.mmieczkowski.serverguard.resourcegroup.request.CreateResourceGroupRequest;
import com.mmieczkowski.serverguard.resourcegroup.response.CreateResourceGroupResponse;
import com.mmieczkowski.serverguard.resourcegroup.response.GetResourceGroupResponse;
import com.mmieczkowski.serverguard.resourcegroup.request.GetResourceGroupPaginatedRequest;
import com.mmieczkowski.serverguard.resourcegroup.response.GetResourceGroupPaginatedResponse;
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
