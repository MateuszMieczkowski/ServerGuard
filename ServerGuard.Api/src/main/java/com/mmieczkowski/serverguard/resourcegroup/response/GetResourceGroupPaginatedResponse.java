package com.mmieczkowski.serverguard.resourcegroup.response;

import java.util.List;
import java.util.UUID;

public record GetResourceGroupPaginatedResponse(List<ResourceGroup> resourceGroups, int pageNumber, int pageSize, int totalPages) {
    public record ResourceGroup(UUID id, String name){
        public ResourceGroup(com.mmieczkowski.serverguard.resourcegroup.model.ResourceGroup resourceGroup) {
            this(resourceGroup.getId(), resourceGroup.getName());
        }
    }
}