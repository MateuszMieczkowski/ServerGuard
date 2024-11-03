using Microsoft.Extensions.Logging;
using Quartz;
using ServerGuard.Agent.Config;
using ServerGuard.Agent.Services;

namespace ServerGuard.Agent.Jobs;

[DisallowConcurrentExecution]
internal sealed class CollectMetricsJob : IJob
{
    private readonly ILogger<CollectMetricsJob> _logger;
    private readonly IMetricCollector _metricCollector;
    private readonly IAgentConfigProvider _agentConfigProvider;
    private readonly IIntegrationApi _integrationApi;
    public CollectMetricsJob(
        ILogger<CollectMetricsJob> logger,
        IMetricCollector metricCollector,
        IAgentConfigProvider agentConfigProvider,
        IIntegrationApi integrationApi)
    {
        _logger = logger;
        _metricCollector = metricCollector;
        _agentConfigProvider = agentConfigProvider;
        _integrationApi = integrationApi;
    }

    public async Task Execute(IJobExecutionContext context)
    {
        _logger.LogInformation("Collecting metrics...");
        var agentConfig = await _agentConfigProvider.GetAsync(context.CancellationToken);
        var metrics = await _metricCollector.CollectAsync();
        await _integrationApi.SendMetrics(agentConfig.ApiKey, agentConfig.ResourceGroupId, agentConfig.AgentId, metrics, context.CancellationToken);
        _logger.LogInformation("Metrics collected");
    }
}
