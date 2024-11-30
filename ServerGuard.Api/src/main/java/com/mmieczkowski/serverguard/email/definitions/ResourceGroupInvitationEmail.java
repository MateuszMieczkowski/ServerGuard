package com.mmieczkowski.serverguard.email.definitions;

import com.mmieczkowski.serverguard.email.EmailDefinition;
import com.mmieczkowski.serverguard.email.EmailPlaceHolder;
import com.mmieczkowski.serverguard.resourcegroup.model.ResourceGroupUserRole;

import java.util.Locale;

public class ResourceGroupInvitationEmail extends EmailDefinition {
    private final String recipientEmail;
    private final String invitationLink;
    private final String resourceGroupName;
    private final ResourceGroupUserRole role;

    public ResourceGroupInvitationEmail(String recipientEmail,
                                        String invitationLink,
                                        String resourceGroupName,
                                        ResourceGroupUserRole role) {
        this.recipientEmail = recipientEmail;
        this.invitationLink = invitationLink;
        this.resourceGroupName = resourceGroupName;
        this.role = role;
    }

    @Override
    public String getTemplateFileName() {
        return "resourceGroupInvitationEmail";
    }

    @Override
    public String getSubject() {
        return "Resource Group Invitation";
    }

    @Override
    public EmailPlaceHolder[] getPlaceHolders() {
        return new EmailPlaceHolder[]{
                new EmailPlaceHolder("{{username}}", recipientEmail),
                new EmailPlaceHolder("{{resourceGroupName}}", resourceGroupName),
                new EmailPlaceHolder("{{invitationLink}}", invitationLink),
                new EmailPlaceHolder("{{role}}", role.toString().toLowerCase(Locale.ROOT))
        };
    }

    @Override
    public String[] getRecipients() {
        return new String[]{recipientEmail};
    }
}
