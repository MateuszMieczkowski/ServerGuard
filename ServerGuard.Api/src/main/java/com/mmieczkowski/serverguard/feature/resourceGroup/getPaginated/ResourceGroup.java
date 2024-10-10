package com.mmieczkowski.serverguard.feature.resourceGroup.getPaginated;

import java.util.UUID;

public record ResourceGroup(UUID id, String name){
    public ResourceGroup(com.mmieczkowski.serverguard.model.ResourceGroup resourceGroup) {
        this(resourceGroup.getId(), resourceGroup.getName());
    }
}
