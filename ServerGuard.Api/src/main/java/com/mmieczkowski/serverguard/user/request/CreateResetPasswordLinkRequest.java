package com.mmieczkowski.serverguard.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateResetPasswordLinkRequest(@NotBlank @Email @Size(min = 5, max=50) String email, @NotBlank @Size(min=1, max = 32) String timeZoneId) {
}
