package com.mmieczkowski.serverguard.resourcegroup.model;

import com.mmieczkowski.serverguard.resourcegroup.exception.ResourceGroupInvitationNotFoundException;
import com.mmieczkowski.serverguard.resourcegroup.exception.UserAlreadyInResourceGroupException;
import com.mmieczkowski.serverguard.user.model.User;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.Assert;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@SQLRestriction("is_deleted = false")
public class ResourceGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @CreatedDate
    @Column(nullable = false)
    private Instant createdDate;

    @LastModifiedDate
    private Instant lastModifiedDate;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "resourceGroup", orphanRemoval = true)
    private final List<UserResourceGroupPermission> userResourceGroupPermissions = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "resource_group_id", nullable = false)
    private final List<ResourceGroupInvitation> resourceGroupInvitations = new ArrayList<>();

    @Column(nullable = false)
    private boolean isDeleted = false;

    public ResourceGroup() {
    }

    public ResourceGroup(String name) {
        Assert.hasText(name, "Name cannot be null or empty");
        this.name = name;
    }

    public void setName(String name) {
        Assert.hasText(name, "Name cannot be null or empty");
        this.name = name;
    }

    public UUID getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public ResourceGroupInvitation createInvitation(String email, ResourceGroupUserRole role, Clock clock) {
        ResourceGroupInvitation invitation = new ResourceGroupInvitation(email, role, clock);
        this.resourceGroupInvitations.add(invitation);
        return invitation;
    }

    public void acceptInvitation(String token, User user, Clock clock) {
        ResourceGroupInvitation resourceGroupInvitation = resourceGroupInvitations.stream()
                .filter(invitation -> invitation.getToken().equals(token))
                .findFirst()
                .orElseThrow(ResourceGroupInvitationNotFoundException::new);

        resourceGroupInvitation.accept(clock);
        addUser(user, resourceGroupInvitation.getRole());
    }

    public void addUser(User user, ResourceGroupUserRole resourceGroupUserRole) {
        if (hasUser(user)) {
            throw new UserAlreadyInResourceGroupException();
        }
        var userResourceGroupPermission = new UserResourceGroupPermission(this, user, resourceGroupUserRole);
        userResourceGroupPermissions.add(userResourceGroupPermission);
    }

    public void removeUser(User user) {
        userResourceGroupPermissions.removeIf(x -> x.getUser().equals(user));
    }

    public ResourceGroupInvitation getInvitation(String token) {
        return resourceGroupInvitations.stream()
                .filter(invitation -> invitation.getToken().equals(token))
                .findFirst()
                .orElseThrow(ResourceGroupInvitationNotFoundException::new);
    }

    public void delete() {
        this.isDeleted = true;
        userResourceGroupPermissions.clear();
    }

    public boolean hasUser(User user) {
        return userResourceGroupPermissions.stream()
                .anyMatch(x -> x.getUser().equals(user));
    }
}
