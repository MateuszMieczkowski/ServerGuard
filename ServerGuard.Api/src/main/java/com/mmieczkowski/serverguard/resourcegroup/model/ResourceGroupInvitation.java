package com.mmieczkowski.serverguard.resourcegroup.model;

import com.mmieczkowski.serverguard.service.SecureRandomString;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@SQLRestriction("expires_at > now() AND accepted_at IS NULL")
@EntityListeners(AuditingEntityListener.class)
public class ResourceGroupInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String email;

    @CreatedDate
    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column
    private Instant acceptedAt;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ResourceGroupUserRole role;

   public ResourceGroupInvitation(String email, ResourceGroupUserRole role, Clock clock){
       this.email = email;
       this.role = role;
       this.expiresAt = clock.instant().plus(3, ChronoUnit.DAYS);
       this.token = SecureRandomString.generate(40);
   }

    public ResourceGroupInvitation() {

    }

    public void accept(Clock clock){
        this.acceptedAt = clock.instant();
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public String constructLink(String webUrl) {
        return webUrl + "/accept-invitation?token=" + this.token;
    }

    public ResourceGroupUserRole getRole() {
        return role;
    }
}