using MassTransit;
using Microsoft.Extensions.Logging;
using Quartz;
using ServerGuard.Agent.Config;
using ServerGuard.Agent.Services;
using ServerGuard.Contracts.Events;

namespace ServerGuard.Agent.Jobs;

[DisallowConcurrentExecution]
internal sealed class CollectMetricsJob : IJob
{
    private readonly ILogger<CollectMetricsJob> _logger;
    private readonly IMetricCollector _metricCollector;
    private readonly IAgentConfigProvider _agentConfigProvider;
    private readonly IPublishEndpoint _publishEndpoint;
    public CollectMetricsJob(
        ILogger<CollectMetricsJob> logger,
        IMetricCollector metricCollector,
        IPublishEndpoint publishEndpoint,
        IAgentConfigProvider agentConfigProvider)
    {
        _logger = logger;
        _metricCollector = metricCollector;
        _publishEndpoint = publishEndpoint;
        _agentConfigProvider = agentConfigProvider;
    }

    public async Task Execute(IJobExecutionContext context)
    {
        _logger.LogInformation("Collecting metrics...");
        var agentConfig = await _agentConfigProvider.GetAsync(context.CancellationToken);
        var metrics = _metricCollector.Collect();
        await _publishEndpoint.Publish(new MetricsCollected(agentConfig.AgentId, metrics), context.CancellationToken);

        _logger.LogInformation("Metrics collected");
    }
}
