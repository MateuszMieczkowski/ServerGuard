package com.mmieczkowski.serverguard.resourcegroup.model;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@SQLRestriction("is_deleted = false")
public class ResourceGroup {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Getter
    @Column(nullable = false)
    private String name;

    @CreatedDate
    private Instant createdDate;

    @LastModifiedDate
    private Instant lastModifiedDate;

    @OneToMany(mappedBy = "resourceGroup")
    private List<UserResourceGroupPermission> userResourceGroupPermissions;

    @Column(nullable = false)
    private final boolean isDeleted = false;

    public ResourceGroup() {}
    public ResourceGroup(String name) {
        this.name = name;
    }

    public void setName(String name) {
        Assert.notNull(name, "Name cannot be null");
        Assert.isTrue(!name.isBlank(), "Name cannot be blank");
        this.name = name;
    }
}
