package com.mmieczkowski.serverguard.auth;

import org.springframework.security.core.Authentication;

public interface AuthTokenService {
    String generateToken(Authentication authentication);
}
