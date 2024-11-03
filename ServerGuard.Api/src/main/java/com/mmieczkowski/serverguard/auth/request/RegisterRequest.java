package com.mmieczkowski.serverguard.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(@NotNull
                              @NotBlank
                              @Email
                              @Size(max = 50)
                              String email,
                              @NotNull
                              @NotBlank
                              @Size(max = 50)
                              String password) {
}
