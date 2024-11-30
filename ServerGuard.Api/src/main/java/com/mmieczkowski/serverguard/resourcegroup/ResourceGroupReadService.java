package com.mmieczkowski.serverguard.resourcegroup;

import com.mmieczkowski.serverguard.annotation.ResourceGroupAccess;
import com.mmieczkowski.serverguard.resourcegroup.request.GetResourceGroupUsersPageRequest;
import com.mmieczkowski.serverguard.resourcegroup.response.GetResourceGroupUsersPageResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ResourceGroupReadService {
    private final ResourceGroupReadRepository resourceGroupReadRepository;

    public ResourceGroupReadService(ResourceGroupReadRepository resourceGroupReadRepository) {
        this.resourceGroupReadRepository = resourceGroupReadRepository;
    }

    @ResourceGroupAccess("id")
    public GetResourceGroupUsersPageResponse getResourceGroupUsersPage(UUID id, GetResourceGroupUsersPageRequest request) {
        var pageRequest = PageRequest.of(request.pageNumber(), request.pageSize())
                .withSort(Sort.by("u.email"));
        var page = resourceGroupReadRepository.findUsersByResourceGroupId(id, pageRequest);
        return new GetResourceGroupUsersPageResponse(page);
    }
}
