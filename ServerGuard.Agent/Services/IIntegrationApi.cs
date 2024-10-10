using Refit;
using ServerGuard.Agent.Dto;

namespace ServerGuard.Agent.Services;

internal interface IIntegrationApi
{
    [Post("/api/metrics")]
    Task SendMetrics([Header("x-api-key")] string apiKey, Metrics metrics, CancellationToken cancellationToken = default);
}
