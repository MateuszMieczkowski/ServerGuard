package com.mmieczkowski.serverguard.resourcegroup.request;

import jakarta.validation.constraints.Min;

public record GetResourceGroupUsersPageRequest(@Min(0) int pageNumber, @Min(0) int pageSize) {
}
