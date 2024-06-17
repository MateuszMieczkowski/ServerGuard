using MassTransit;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Options;
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
    private readonly AgentOptions _options;
    private readonly IPublishEndpoint _publishEndpoint;
    public CollectMetricsJob(
        ILogger<CollectMetricsJob> logger,
        IMetricCollector metricCollector,
        IOptions<AgentOptions> options,
        IPublishEndpoint publishEndpoint)
    {
        _logger = logger;
        _metricCollector = metricCollector;
        _options = options.Value;
        _publishEndpoint = publishEndpoint;
    }

    public async Task Execute(IJobExecutionContext context)
    {
        _logger.LogInformation("Collecting metrics...");

        var metrics = _metricCollector.Collect();
        await _publishEndpoint.Publish(new MetricsCollected(Guid.NewGuid(), metrics), context.CancellationToken);

        _logger.LogInformation("Metrics collected");
    }
}
