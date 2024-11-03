package com.mmieczkowski.serverguard.agent.response;

import java.util.UUID;

public record CreateAgentResponse(UUID id, String name) {
}
