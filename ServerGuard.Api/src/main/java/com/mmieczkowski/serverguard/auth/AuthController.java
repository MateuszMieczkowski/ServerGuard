package com.mmieczkowski.serverguard.auth;

import com.mmieczkowski.serverguard.auth.request.LoginRequest;
import com.mmieczkowski.serverguard.auth.response.LoginResponse;
import com.mmieczkowski.serverguard.auth.request.RegisterRequest;
import com.mmieczkowski.serverguard.auth.response.RegisterResponse;
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
