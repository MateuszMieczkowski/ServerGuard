package com.mmieczkowski.serverguard.resourcegroup.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = org.springframework.http.HttpStatus.BAD_REQUEST, reason = "Cannot delete yourself")
public class CannotDeleteYourselfException extends RuntimeException {

}
