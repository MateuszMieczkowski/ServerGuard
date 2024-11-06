package com.mmieczkowski.serverguard.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "smtp")
public record SmtpProperties(String host, int port, String username, String password) {
}
