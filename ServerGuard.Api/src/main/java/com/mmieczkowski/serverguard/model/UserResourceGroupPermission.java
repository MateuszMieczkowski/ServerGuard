package com.mmieczkowski.serverguard.model;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

@Entity
@SQLRestriction("is_deleted = false")
public class UserResourceGroupPermission implements GrantedAuthority, Serializable {

    public UserResourceGroupPermission() {}

    public UserResourceGroupPermission(ResourceGroup resourceGroup,
                                       User user,
                                       ResourceGroupUserRole role) {
        id = new UserResourceGroupPermissionPK(user.getId(), resourceGroup.getId());
        this.resourceGroup = resourceGroup;
        this.user = user;
        this.role = role;
    }

    @EmbeddedId
    private UserResourceGroupPermissionPK id;

    @ManyToOne
    @MapsId("userId")
    private User user;

    @ManyToOne
    @MapsId("resourceGroupId")
    private ResourceGroup resourceGroup;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceGroupUserRole role;

    @Column(nullable = false)
    private boolean isDeleted = false;


    public ResourceGroup getResourceGroup() {
        return resourceGroup;
    }

    public void setResourceGroup(ResourceGroup resourceGroup) {
        this.resourceGroup = resourceGroup;
    }

    @Override
    public String getAuthority() {
        return String.format("resourceGroup_%s_%s", resourceGroup.getId(), role.toString());
    }
}

