package com.mmieczkowski.serverguard.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = org.springframework.http.HttpStatus.FORBIDDEN, reason = "No access to resource group")
public class NoAccessToResourceGroupException extends RuntimeException {
}
