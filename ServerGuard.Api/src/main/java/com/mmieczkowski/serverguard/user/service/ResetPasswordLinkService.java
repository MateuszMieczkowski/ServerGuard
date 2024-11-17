package com.mmieczkowski.serverguard.user.service;

import com.mmieczkowski.serverguard.config.properties.WebProperties;
import com.mmieczkowski.serverguard.email.EmailService;
import com.mmieczkowski.serverguard.email.definitions.ResetPasswordEmail;
import com.mmieczkowski.serverguard.user.ResetPasswordLinkNotFoundException;
import com.mmieczkowski.serverguard.user.repository.UserRepository;
import com.mmieczkowski.serverguard.user.model.ResetPasswordLink;
import com.mmieczkowski.serverguard.user.model.User;
import com.mmieczkowski.serverguard.user.request.CreateResetPasswordLinkRequest;
import com.mmieczkowski.serverguard.user.request.ResetPasswordRequest;
import jakarta.mail.MessagingException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Clock;
import java.util.TimeZone;

@Service
public class ResetPasswordLinkService {
    private final UserRepository userRepository;
    private final Clock clock;
    private final WebProperties webProperties;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public ResetPasswordLinkService(UserRepository userRepository,
                                    Clock clock,
                                    WebProperties webProperties,
                                    PasswordEncoder passwordEncoder,
                                    EmailService emailService) {
        this.userRepository = userRepository;
        this.clock = clock;
        this.webProperties = webProperties;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public void create(CreateResetPasswordLinkRequest request) {
        var optionalUser = userRepository.findByEmail(request.email());
        if (optionalUser.isEmpty()) {
            return;
        }
        User user = optionalUser.get();
        ResetPasswordLink resetPasswordLink = user.createResetPasswordLink(clock);
        try {
            var timeZone = TimeZone.getTimeZone(request.timeZoneId());
            sendEmail(resetPasswordLink, timeZone);
        } catch (IOException | MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendEmail(ResetPasswordLink link, TimeZone timeZone) throws MessagingException, IOException {
        String webUrl = webProperties.url();
        var resetUrl = link.constructLink(webUrl);
        var email = new ResetPasswordEmail(link.getUserEmail(), resetUrl, link.getRequestedAt(timeZone));
        emailService.sendEmail(email);
    }

    public void reset(ResetPasswordRequest request) {
        User user = userRepository.findFirstByResetPasswordLinkToken(request.token())
                .orElseThrow(ResetPasswordLinkNotFoundException::new);
        var newPassword = passwordEncoder.encode(request.newPassword());
        user.setPassword(newPassword);
        user.markResetPasswordLinkAsUsed(request.token(), clock);
        userRepository.save(user);
    }

}
