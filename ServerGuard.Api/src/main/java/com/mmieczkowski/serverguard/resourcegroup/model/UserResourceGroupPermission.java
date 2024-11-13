package com.mmieczkowski.serverguard.resourcegroup.model;

import com.mmieczkowski.serverguard.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
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

    @ManyToOne
    @MapsId("userId")
    private User user;

    @Setter
    @Getter
    @ManyToOne
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
}
