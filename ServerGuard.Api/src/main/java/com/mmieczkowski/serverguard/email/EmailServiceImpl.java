package com.mmieczkowski.serverguard.email;

import com.mmieczkowski.serverguard.config.properties.SmtpProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final EmailTemplateProvider emailTemplateProvider;
    private final SmtpProperties smtpProperties;

    public EmailServiceImpl(JavaMailSender javaMailSender, EmailTemplateProvider emailTemplateProvider, SmtpProperties smtpProperties) {
        this.mailSender = javaMailSender;
        this.emailTemplateProvider = emailTemplateProvider;
        this.smtpProperties = smtpProperties;
    }


    @Override
    public void sendEmail(EmailDefinition emailDefinition) throws MessagingException, IOException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setFrom(smtpProperties.username());
        helper.setTo(emailDefinition.getRecipients());
        helper.setSubject(emailDefinition.getSubject());
        String template = emailTemplateProvider.getTemplate(emailDefinition.getTemplateFileName());
        EmailPlaceHolder[] placeHolders = emailDefinition.getPlaceHolders();
        String emailContent = template;
        for(EmailPlaceHolder placeHolder : placeHolders) {
            emailContent = emailContent.replace(placeHolder.key(), placeHolder.value());
        }

        helper.setText(emailContent, true);
        mailSender.send(mimeMessage);
    }
}
