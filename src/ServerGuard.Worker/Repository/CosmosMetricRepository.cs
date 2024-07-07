using Microsoft.Azure.Cosmos;
using ServerGuard.Contracts.Dto;

namespace ServerGuard.Worker.Repository;

internal sealed class CosmosMetricRepository : IMetricRepository
{
    private readonly CosmosClient _cosmosClient;

    public CosmosMetricRepository(CosmosClient cosmosClient)
    {
        _cosmosClient = cosmosClient;
    }

    public async Task InsertAsync(Guid agentId, Metrics metrics, CancellationToken cancellationToken = default)
    {
        Database database = _cosmosClient.GetDatabase("serverguard-db");
        Container container = database.GetContainer("AgentMetric");
    
        var insertTasks = new List<Task>();
        foreach (var sensorMetric in metrics.SensorMetrics)
        {
            foreach (var metric in sensorMetric.Metrics)
            {
                var item = new AgentMetric(Guid.NewGuid(), agentId, sensorMetric.Name, metric.Name, metrics.Time, metric.Value ?? 0, (int)metric.Type);
                var insertTask = container.CreateItemAsync(item, partitionKey: new PartitionKey(agentId.ToString()), cancellationToken: cancellationToken);
                insertTasks.Add(insertTask);
            }
        }
        await Task.WhenAll(insertTasks);
    }
    private sealed record AgentMetric(Guid id, Guid AgentId, string SensorName, string Name, DateTimeOffset Timestamp, float Value, int Type);
}
