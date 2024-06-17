using MassTransit;
using Microsoft.Extensions.Logging;
using ServerGuard.Contracts.Events;

namespace ServerGuard.Worker.Consumers;

internal sealed class MetricsCollectedConsumer : IConsumer<MetricsCollected>
{
    private readonly ILogger<MetricsCollectedConsumer> _logger;

    public MetricsCollectedConsumer(ILogger<MetricsCollectedConsumer> logger)
    {
        _logger = logger;
    }

    public async Task Consume(ConsumeContext<MetricsCollected> context)
    {
        await Task.CompletedTask;
        _logger.LogInformation("Metrics collected: {Metrics}", context.Message);
    }
}
