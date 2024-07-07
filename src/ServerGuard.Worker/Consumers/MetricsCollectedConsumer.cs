using MassTransit;
using Microsoft.Extensions.Logging;
using ServerGuard.Contracts.Events;
using ServerGuard.Worker.Repository;

namespace ServerGuard.Worker.Consumers;

internal sealed class MetricsCollectedConsumer : IConsumer<MetricsCollected>
{
    private readonly ILogger<MetricsCollectedConsumer> _logger;
    private readonly IMetricRepository _metricRepository;
    public MetricsCollectedConsumer(ILogger<MetricsCollectedConsumer> logger, IMetricRepository metricRepository)
    {
        _logger = logger;
        _metricRepository = metricRepository;
    }

    public async Task Consume(ConsumeContext<MetricsCollected> context)
    {
        await _metricRepository.InsertAsync(context.Message.AgentId, context.Message.Metrics, context.CancellationToken);
    }
}
