package com.mmieczkowski.serverguard.user.model;

import com.mmieczkowski.serverguard.service.SecureRandomString;
import io.swagger.v3.oas.models.info.Contact;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.*;
import java.util.TimeZone;
import java.util.UUID;

@Entity
@SQLRestriction("expires_at > now() AND used_at IS NULL")
public class ResetPasswordLink {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private User user;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private Instant requestedAt;

    @Column
    private Instant usedAt;

    public ResetPasswordLink() {
    }

    public ResetPasswordLink(User user, Clock clock) {
        this.user = user;
        this.token = SecureRandomString.generate(64);
        this.expiresAt = Instant.now(clock).plus(1, java.time.temporal.ChronoUnit.HOURS);
        this.requestedAt = Instant.now(clock);
    }

    public String constructLink(String apiBaseUrl) {
        return apiBaseUrl + "/reset-password?token=" + this.token;
    }

    public LocalDateTime getRequestedAt(TimeZone timeZone) {
        return LocalDateTime.ofInstant(this.requestedAt, timeZone.toZoneId());
    }

    public String getUserEmail() {
        return user.getUsername();
    }

    public String getToken() {
        return token;
    }

    public void markAsUsed(Clock clock) {
        this.usedAt = Instant.now(clock);
    }
}
