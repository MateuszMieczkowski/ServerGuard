package com.mmieczkowski.serverguard.user.request;

public record ResetPasswordRequest(String token, String newPassword) {
}
