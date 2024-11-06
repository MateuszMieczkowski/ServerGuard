using Refit;
using ServerGuard.Agent.Config;
using ServerGuard.Agent.Dto;

namespace ServerGuard.Agent.Services;

internal interface IIntegrationApi
{
    [Post("/api/resourceGroups/{resourceGroupId}/agents/{agentId}/metrics")]
    Task SendMetrics([Header("x-api-key")] string apiKey, [AliasAs("resourceGroupId")] Guid resourceGroupId, [AliasAs("agentId")] Guid agentId, Metrics metrics, CancellationToken cancellationToken = default);

    [Get("/api/agentConfig")]
    Task<AgentConfig> GetAgentConfigAsync([Header("x-api-key")] string apiKey, CancellationToken cancellationToken = default);
}
