using Microsoft.Extensions.Configuration;
using Npgsql;
using NpgsqlTypes;
using ServerGuard.Contracts.Dto;

namespace ServerGuard.Worker.Repository;
internal sealed class PostgresMetricRepository : IMetricRepository
{
    private readonly IConfiguration _configuration;

    public PostgresMetricRepository(IConfiguration configuration)
    {
        _configuration = configuration;
    }

    public async Task InsertAsync(Guid agentId, Metrics metrics, CancellationToken cancellationToken = default)
    {
        string connectionString = _configuration.GetConnectionString("Timescale")!;
        var dataSourceBuilder = new NpgsqlDataSourceBuilder(connectionString);
        await using var dataSource = dataSourceBuilder.Build();
        var connection = await dataSource.OpenConnectionAsync(cancellationToken);

        await using var writer = connection.BeginBinaryImport(@"
        COPY ""AgentMetric""
        (""AgentId"", ""SensorName"", ""Name"", ""Timestamp"", ""Value"", ""Type"")
        FROM STDIN (FORMAT BINARY)"
        );

        foreach (var sensorMetric in metrics.SensorMetrics)
        {
            foreach (var metric in sensorMetric.Metrics)
            {
                await writer.StartRowAsync(cancellationToken);
                await writer.WriteAsync(agentId, NpgsqlDbType.Uuid, cancellationToken);
                await writer.WriteAsync(sensorMetric.Name, NpgsqlDbType.Text, cancellationToken);
                await writer.WriteAsync(metric.Name, NpgsqlDbType.Text, cancellationToken);
                await writer.WriteAsync(metrics.Time, NpgsqlDbType.TimestampTz, cancellationToken);
                await writer.WriteAsync(metric.Value ?? 0, NpgsqlDbType.Real, cancellationToken);
                await writer.WriteAsync((int)metric.Type, NpgsqlDbType.Integer, cancellationToken); // You may need to handle the enum conversion
            }
        }

        await writer.CompleteAsync(cancellationToken);
        await connection.CloseAsync();
    }
}
