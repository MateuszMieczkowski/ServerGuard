package com.mmieczkowski.serverguard.feature.auth.service;

import org.springframework.security.core.Authentication;

public interface AuthTokenService {
    String generateToken(Authentication authentication);
}
