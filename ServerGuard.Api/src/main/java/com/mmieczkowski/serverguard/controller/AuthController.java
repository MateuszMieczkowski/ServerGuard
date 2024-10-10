package com.mmieczkowski.serverguard.controller;

import com.mmieczkowski.serverguard.feature.auth.login.LoginRequest;
import com.mmieczkowski.serverguard.feature.auth.login.LoginResponse;
import com.mmieczkowski.serverguard.feature.auth.register.RegisterRequest;
import com.mmieczkowski.serverguard.feature.auth.register.RegisterResponse;
import com.mmieczkowski.serverguard.feature.auth.service.AuthService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("login")
    @ResponseBody
    public LoginResponse login(@Validated @RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("register")
    @ResponseBody
    public RegisterResponse register(@Validated @RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }
}
