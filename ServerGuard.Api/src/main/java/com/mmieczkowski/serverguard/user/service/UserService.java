package com.mmieczkowski.serverguard.user.service;

import com.mmieczkowski.serverguard.service.CurrentUserService;
import com.mmieczkowski.serverguard.user.repository.UserRepository;
import com.mmieczkowski.serverguard.user.request.LoginRequest;
import com.mmieczkowski.serverguard.user.response.LoginResponse;
import com.mmieczkowski.serverguard.user.request.RegisterRequest;
import com.mmieczkowski.serverguard.user.response.RegisterResponse;
import com.mmieczkowski.serverguard.user.model.User;
import com.mmieczkowski.serverguard.user.response.UserProfileResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final AuthTokenService authTokenService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    public UserService(
            AuthTokenService authTokenService,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository, CurrentUserService currentUserService) {
        this.authTokenService = authTokenService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
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
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            return new RegisterResponse(false);
        }
        return new RegisterResponse(true);
    }

    public UserProfileResponse getUserProfile() {
        User user = currentUserService.getLoggedInUser().orElseThrow();
        return new UserProfileResponse(user.getId(), user.getUsername(), user.getPermissions()
                .map(x -> new UserProfileResponse.ResourceGroupPermission(x.getId().getResourceGroupId(), x.getRole()) ).toList());
    }
}
