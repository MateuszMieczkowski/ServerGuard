package com.mmieczkowski.serverguard.resourcegroup;

import com.mmieczkowski.serverguard.resourcegroup.request.CreateResourceGroupInvitationRequest;
import com.mmieczkowski.serverguard.resourcegroup.request.CreateResourceGroupRequest;
import com.mmieczkowski.serverguard.resourcegroup.request.GetResourceGroupUsersPageRequest;
import com.mmieczkowski.serverguard.resourcegroup.response.CreateResourceGroupResponse;
import com.mmieczkowski.serverguard.resourcegroup.response.GetResourceGroupResponse;
import com.mmieczkowski.serverguard.resourcegroup.request.GetResourceGroupPaginatedRequest;
import com.mmieczkowski.serverguard.resourcegroup.response.GetResourceGroupPaginatedResponse;
import com.mmieczkowski.serverguard.resourcegroup.response.GetResourceGroupUsersPageResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/resourceGroups")
public class ResourceGroupController {

    private final ResourceGroupService resourceGroupService;
    private final ResourceGroupReadService resourceGroupReadService;

    public ResourceGroupController(ResourceGroupService resourceGroupService,
                                   ResourceGroupReadService resourceGroupReadService) {
        this.resourceGroupService = resourceGroupService;
        this.resourceGroupReadService = resourceGroupReadService;
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
    public GetResourceGroupPaginatedResponse getResourceGroupsPaginated(GetResourceGroupPaginatedRequest request) {
        return resourceGroupService.getResourceGroups(request);
    }

    @DeleteMapping("{id}")
    public void deleteResourceGroup(@PathVariable UUID id) {
        resourceGroupService.deleteResourceGroup(id);
    }

    @PutMapping("{id}")
    public void updateResourceGroup(@PathVariable UUID id, @RequestBody CreateResourceGroupRequest request) {
        resourceGroupService.updateResourceGroup(id, request);
    }

    @PostMapping("{id}/invitations")
    public void createResourceGroupInvitation(@PathVariable UUID id, @Valid @RequestBody CreateResourceGroupInvitationRequest request) {
        resourceGroupService.createResourceGroupInvitation(id, request);
    }

    @PostMapping("{id}/invitations/{token}")
    public void acceptResourceGroupInvitation(@PathVariable UUID id, @PathVariable String token) {
        resourceGroupService.acceptResourceGroupInvitation(id, token);
    }

    @GetMapping("{id}/users")
    public GetResourceGroupUsersPageResponse getResourceGroupUsersPage(@PathVariable UUID id, @Valid GetResourceGroupUsersPageRequest request) {
        return resourceGroupReadService.getResourceGroupUsersPage(id, request);
    }

    @DeleteMapping("{id}/users/{userId}")
    public void removeUserFromResourceGroup(@PathVariable UUID id, @PathVariable UUID userId) {
        resourceGroupService.removeUserFromResourceGroup(id, userId);
    }

}
