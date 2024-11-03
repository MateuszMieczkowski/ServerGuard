package com.mmieczkowski.serverguard.dashboard.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = org.springframework.http.HttpStatus.NOT_FOUND, reason = "Dashboard not found")
public class DashboardNotFoundException extends RuntimeException {

}
