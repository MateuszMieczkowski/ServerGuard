package com.mmieczkowski.serverguard.resourcegroup.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "ResourceGroup not found")
public class ResourceGroupNotFoundException extends RuntimeException {
    public ResourceGroupNotFoundException(UUID resourceGroupId)  {
        super(String.format("ResourceGroup %s not found", resourceGroupId));
    }
}
