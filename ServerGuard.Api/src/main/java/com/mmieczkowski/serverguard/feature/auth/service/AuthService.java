package com.mmieczkowski.serverguard.feature.auth.service;

import com.mmieczkowski.serverguard.feature.auth.login.LoginRequest;
import com.mmieczkowski.serverguard.feature.auth.login.LoginResponse;
import com.mmieczkowski.serverguard.feature.auth.register.RegisterRequest;
import com.mmieczkowski.serverguard.feature.auth.register.RegisterResponse;
import com.mmieczkowski.serverguard.model.User;
import com.mmieczkowski.serverguard.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthTokenService authTokenService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthService(
            AuthTokenService authTokenService,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository) {
        this.authTokenService = authTokenService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        String token = authTokenService.generateToken(authentication);
        return new LoginResponse(token);
    }

    public RegisterResponse register(RegisterRequest registerRequest) {

        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(registerRequest.email())
                .password(registerRequest.password())
                .authorities("ROLE_USER")
                .passwordEncoder(passwordEncoder::encode)
                .build();
        User user = new User(userDetails);
        userRepository.save(user);
        return new RegisterResponse(true);
    }
}
