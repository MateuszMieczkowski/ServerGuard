package com.mmieczkowski.serverguard.user;

import com.mmieczkowski.serverguard.resourcegroup.model.UserResourceGroupPermission;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "app_user")
public class User implements UserDetails {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private List<UserResourceGroupPermission> permissions;

    public User() {
    }

    public User(UserDetails userDetails) {
        this.email = userDetails.getUsername();
        this.password = userDetails.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public boolean hasAccessToResourceGroup(UUID resourceGroupId) {
        return permissions.stream()
                .anyMatch(permission -> permission.getResourceGroup().getId().equals(resourceGroupId));
    }
}
