using Ardalis.SharedKernel;
using ServerGuard.Core.Enums;
using ServerGuard.Core.Models.AgentAggregate;

namespace ServerGuard.Core.Models.AgentMetricAggregate;

public class AgentMetric : IAggregateRoot
{
    public AgentId AgentId { get; private set; }
    public string SensorName { get; private set; } = default!;
    public string Name { get; private set; } = default!;
    public float? Value { get; private set; }
    public MetricType Type { get; private set; }
    public DateTime Timestamp { get; private set; }

    private AgentMetric() { }

    public AgentMetric(
        AgentId agentId,
        string hardwareName,
        string name,
        MetricType type,
        float? value,
        DateTime timestamp)
    {
        AgentId = agentId;
        Name = name;
        SensorName = hardwareName;
        Type = type;
        Value = value;
        Timestamp = timestamp;
    }
}
