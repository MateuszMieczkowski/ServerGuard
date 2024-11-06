package com.mmieczkowski.serverguard.agent.response;

import java.util.UUID;

public record GetAgentConfigResponse(UUID resourceGroupId,
                                     UUID agentId,
                                     boolean isCpuEnabled,
                                     boolean isGpuEnabled,
                                     boolean isMemoryEnabled,
                                     boolean isMotherboardEnabled,
                                     boolean isControllerEnabled,
                                     boolean isNetworkEnabled,
                                     boolean isStorageEnabled,
                                     int collectEverySeconds
) {
}
