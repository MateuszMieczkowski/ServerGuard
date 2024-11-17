package com.mmieczkowski.serverguard.email.definitions;

import com.mmieczkowski.serverguard.email.EmailDefinition;
import com.mmieczkowski.serverguard.email.EmailPlaceHolder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ResetPasswordEmail extends EmailDefinition {

    private final String userEmail;
    private final String resetPasswordLink;
    private final LocalDateTime requestedAt;

    public ResetPasswordEmail(String userEmail, String resetPasswordLink, LocalDateTime requestedAt) {
        this.userEmail = userEmail;
        this.resetPasswordLink = resetPasswordLink;
        this.requestedAt = requestedAt;
    }

    @Override
    public String getTemplateFileName() {
        return "resetPasswordEmail";
    }

    @Override
    public String getSubject() {
        return "Reset your password";
    }

    @Override
    public EmailPlaceHolder[] getPlaceHolders() {
        return new EmailPlaceHolder[]{
                new EmailPlaceHolder("{{userName}}", userEmail),
                new EmailPlaceHolder("{{resetLink}}", resetPasswordLink),
                new EmailPlaceHolder("{{requestedAt}}", requestedAt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")))
        };
    }

    @Override
    public String[] getRecipients() {
        return new String[]{userEmail};
    }
}
