using MassTransit;
using Microsoft.Extensions.Logging;
using Quartz;
using ServerGuard.Agent.Options;
using ServerGuard.Agent.Services;
using ServerGuard.Contracts.Events;

namespace ServerGuard.Agent.Jobs;

[DisallowConcurrentExecution]
internal sealed class CollectMetricsJob : IJob
{
    private readonly ILogger<CollectMetricsJob> _logger;
    private readonly IMetricCollector _metricCollector;
    private readonly AgentConfig _agentConfig;
    private readonly IPublishEndpoint _publishEndpoint;
    public CollectMetricsJob(
        ILogger<CollectMetricsJob> logger,
        IMetricCollector metricCollector,
        AgentConfig agentConfig,
        IPublishEndpoint publishEndpoint)
    {
        _logger = logger;
        _metricCollector = metricCollector;
        _agentConfig = agentConfig;
        _publishEndpoint = publishEndpoint;
    }

    public async Task Execute(IJobExecutionContext context)
    {
        _logger.LogInformation("Collecting metrics...");

        var metrics = _metricCollector.Collect();
        await _publishEndpoint.Publish(new MetricsCollected(_agentConfig.AgentId, metrics), context.CancellationToken);

        _logger.LogInformation("Metrics collected");
    }
}
