using ClickHouse.Client.ADO;
using ClickHouse.Client.Utility;
using Microsoft.Extensions.Configuration;
using ServerGuard.Contracts.Dto;

namespace ServerGuard.Worker.Repository;
internal sealed class ClickHouseMetricRepository : IMetricRepository
{
    private const string AgentMetricTableName = "AgentMetric";
    private readonly IConfiguration _configuration;

    public ClickHouseMetricRepository(IConfiguration configuration)
    {
        _configuration = configuration;
    }

    public async Task InsertAsync(Guid agentId, Metrics metrics, CancellationToken cancellationToken = default)
    {
        var connectionString = _configuration.GetConnectionString("ClickHouse")!;
        using var connection = new ClickHouseConnection(connectionString);
        var sql = $"""INSERT INTO {AgentMetricTableName} (AgentId, Time, SensorName, MetricName, Value, Type) VALUES""";

        foreach (var sensorMetric in metrics.SensorMetrics)
        {
            foreach (var metric in sensorMetric.Metrics)
            {
                sql += $" ('{agentId}', '{metrics.Time.ToString("O")}', '{sensorMetric.Name}', '{metric.Name}', {metric.Value ?? 0f}, '{((int)metric.Type)}'),";
            }
        }
        await connection.ExecuteStatementAsync(sql.TrimEnd(','));
    }

}
