package com.mmieczkowski.serverguard.agent.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(reason = "Agent not found", code = org.springframework.http.HttpStatus.NOT_FOUND)
public class AgentNotFoundException extends RuntimeException {
    public AgentNotFoundException() {
    }
}
