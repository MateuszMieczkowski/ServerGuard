package com.mmieczkowski.serverguard.user.service;

import org.springframework.security.core.Authentication;

public interface AuthTokenService {
    String generateToken(Authentication authentication);
}
