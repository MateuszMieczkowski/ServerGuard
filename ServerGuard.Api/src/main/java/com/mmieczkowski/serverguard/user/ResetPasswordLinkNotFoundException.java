package com.mmieczkowski.serverguard.user;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(reason = "Reset password link not found", code = org.springframework.http.HttpStatus.NOT_FOUND)
public class ResetPasswordLinkNotFoundException extends RuntimeException {
}
