package com.mmieczkowski.serverguard.dashboard.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record GetGraphDataRequest(
        @NotNull int maxDataPoints,
        @NotNull LocalDateTime from,
        @NotNull LocalDateTime to,
        @NotNull String aggregationType
) {
}
