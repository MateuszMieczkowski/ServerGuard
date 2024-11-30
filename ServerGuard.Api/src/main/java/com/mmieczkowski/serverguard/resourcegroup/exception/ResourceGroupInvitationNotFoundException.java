package com.mmieczkowski.serverguard.resourcegroup.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Resource group invitation not found")
public class ResourceGroupInvitationNotFoundException extends RuntimeException {
}
