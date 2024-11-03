package com.mmieczkowski.serverguard.dashboard.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = org.springframework.http.HttpStatus.NOT_FOUND, reason = "Graph not found")
public class GraphNotFoundException extends RuntimeException{
}
