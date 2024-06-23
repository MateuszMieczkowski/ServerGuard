using MassTransit;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;
using Npgsql;
using NpgsqlTypes;
using ServerGuard.Contracts.Events;

namespace ServerGuard.Worker.Consumers;

internal sealed class MetricsCollectedConsumer : IConsumer<MetricsCollected>
{
    private readonly ILogger<MetricsCollectedConsumer> _logger;
    private readonly IConfiguration _configuration;
    public MetricsCollectedConsumer(ILogger<MetricsCollectedConsumer> logger, IConfiguration configuration)
    {
        _logger = logger;
        _configuration = configuration;
    }

    public async Task Consume(ConsumeContext<MetricsCollected> context)
    {
        string connectionString = _configuration.GetConnectionString("Timescale")!;
        var dataSourceBuilder = new NpgsqlDataSourceBuilder(connectionString);
        await using var dataSource = dataSourceBuilder.Build();
        var connection = await dataSource.OpenConnectionAsync(context.CancellationToken);

        await using var writer = connection.BeginBinaryImport(@"
        COPY ""AgentMetric""
        (""AgentId"", ""SensorName"", ""Name"", ""Timestamp"", ""Value"", ""Type"")
        FROM STDIN (FORMAT BINARY)");

        var agentId = context.Message.AgentId;
        foreach (var sensorMetric in context.Message.Metrics.SensorMetrics)
        {
            foreach (var metric in sensorMetric.Metrics)
            {
                await writer.StartRowAsync(context.CancellationToken);
                await writer.WriteAsync(agentId, NpgsqlDbType.Uuid);
                await writer.WriteAsync(sensorMetric.Name, NpgsqlDbType.Text);
                await writer.WriteAsync(metric.Name, NpgsqlDbType.Text);
                await writer.WriteAsync(context.Message.Metrics.Time, NpgsqlDbType.TimestampTz);
                await writer.WriteAsync(metric.Value ?? 0, NpgsqlDbType.Real);
                await writer.WriteAsync((int)metric.Type, NpgsqlDbType.Integer); // You may need to handle the enum conversion

            }
        }

        await writer.CompleteAsync(context.CancellationToken);
        await connection.CloseAsync();
    }
}
