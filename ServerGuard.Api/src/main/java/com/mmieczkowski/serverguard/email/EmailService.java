package com.mmieczkowski.serverguard.email;

import jakarta.mail.MessagingException;

import java.io.IOException;

public interface EmailService {
    void sendEmail(EmailDefinition emailDefinition) throws MessagingException, IOException;
}
