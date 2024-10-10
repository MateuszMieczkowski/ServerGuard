package com.mmieczkowski.serverguard.feature.agent.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(reason = "Failed to create agent", code = HttpStatus.INTERNAL_SERVER_ERROR)
public class FailedToCreateAgentException extends RuntimeException {
    public FailedToCreateAgentException() {
    }
}
