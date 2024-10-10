package com.mmieczkowski.serverguard.feature.resourceGroup.getPaginated;

import java.util.List;

public record GetResourceGroupPaginatedResponse(List<ResourceGroup> resourceGroups, int pageNumber, int pageSize, int totalPages) {

}