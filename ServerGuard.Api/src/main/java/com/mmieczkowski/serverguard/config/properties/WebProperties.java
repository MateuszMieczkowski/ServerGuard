package com.mmieczkowski.serverguard.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "web")
public record WebProperties(String url) {
}
