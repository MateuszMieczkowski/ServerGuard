package com.mmieczkowski.serverguard.resourcegroup.model;

import com.mmieczkowski.serverguard.user.model.User;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;

@Entity
@SQLRestriction("is_deleted = false")
public class UserResourceGroupPermission implements GrantedAuthority {

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

    @Column(nullable = false)
    private final boolean isDeleted = false;

    @Override
    public String getAuthority() {
        return String.format("resourceGroup_%s_%s", resourceGroup.getId(), role.toString());
    }

    public ResourceGroup getResourceGroup() {
        return this.resourceGroup;
    }

    public User getUser() {
        return this.user;
    }

    public ResourceGroupUserRole getRole() {
        return this.role;
    }
}
