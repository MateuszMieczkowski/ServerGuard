package com.mmieczkowski.serverguard.agent.request;

import java.util.UUID;

public record GetAgentRequest(UUID resourceGroupId, UUID id) {
}
