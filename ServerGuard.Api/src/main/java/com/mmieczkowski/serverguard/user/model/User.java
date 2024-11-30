package com.mmieczkowski.serverguard.user.model;

import com.mmieczkowski.serverguard.resourcegroup.model.UserResourceGroupPermission;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "app_user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private List<UserResourceGroupPermission> permissions;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<ResetPasswordLink> resetPasswordLinks;

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

    public UUID getId() {
        return this.id;
    }

    public ResetPasswordLink createResetPasswordLink(Clock clock) {
        ResetPasswordLink resetPasswordLink = new ResetPasswordLink(this, clock);
        resetPasswordLinks.add(resetPasswordLink);
        return resetPasswordLink;
    }

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

    public void markResetPasswordLinkAsUsed(String token, Clock clock) {
        resetPasswordLinks.stream()
                .filter(link -> link.getToken().equals(token))
                .findFirst()
                .ifPresent(x -> x.markAsUsed(clock));
    }
}
