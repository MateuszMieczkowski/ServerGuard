package com.mmieczkowski.serverguard.resourcegroup.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(reason = "User is already in resource group", code = HttpStatus.BAD_REQUEST)
public class UserAlreadyInResourceGroupException extends RuntimeException {
}
