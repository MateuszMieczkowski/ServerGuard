package com.mmieczkowski.serverguard.user;

import com.mmieczkowski.serverguard.user.request.CreateResetPasswordLinkRequest;
import com.mmieczkowski.serverguard.user.request.LoginRequest;
import com.mmieczkowski.serverguard.user.request.ResetPasswordRequest;
import com.mmieczkowski.serverguard.user.response.LoginResponse;
import com.mmieczkowski.serverguard.user.request.RegisterRequest;
import com.mmieczkowski.serverguard.user.response.RegisterResponse;
import com.mmieczkowski.serverguard.user.service.ResetPasswordLinkService;
import com.mmieczkowski.serverguard.user.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;
    private final ResetPasswordLinkService resetPasswordLinkService;

    public UserController(UserService userService, ResetPasswordLinkService resetPasswordLinkService) {
        this.userService = userService;
        this.resetPasswordLinkService = resetPasswordLinkService;
    }

    @PostMapping("login")
    @ResponseBody
    public LoginResponse login(@Validated @RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest);
    }

    @PostMapping("register")
    @ResponseBody
    public RegisterResponse register(@Validated @RequestBody RegisterRequest registerRequest) {
        return userService.register(registerRequest);
    }

    @PostMapping("reset-password-link")
    @ResponseBody
    public void createResetPasswordLink(@Validated @RequestBody CreateResetPasswordLinkRequest request) {
        resetPasswordLinkService.create(request);
    }

    @PostMapping("reset-password")
    @ResponseBody
    public void resetPassword(@Validated @RequestBody ResetPasswordRequest request) {
        resetPasswordLinkService.reset(request);
    }
}
