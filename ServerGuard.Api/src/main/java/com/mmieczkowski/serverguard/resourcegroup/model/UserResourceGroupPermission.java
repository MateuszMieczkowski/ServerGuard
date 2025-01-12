package com.mmieczkowski.serverguard.resourcegroup.model;

import com.mmieczkowski.serverguard.user.model.User;
import jakarta.persistence.*;

@Entity
public class UserResourceGroupPermission {

    public UserResourceGroupPermission() {
    }

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

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("userId")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("resourceGroupId")
    private ResourceGroup resourceGroup;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceGroupUserRole role;

    public User getUser() {
        return this.user;
    }

    public ResourceGroupUserRole getRole() {
        return this.role;
    }

    public UserResourceGroupPermissionPK getId() {
        return this.id;
    }
}
