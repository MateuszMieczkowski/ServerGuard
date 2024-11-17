package com.mmieczkowski.serverguard.dashboard.request;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record GetGraphDataRequest(
        @NotNull int maxDataPoints,
        @NotNull Instant from,
        @NotNull Instant to,
        @NotNull String aggregationType
) {
}
