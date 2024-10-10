namespace ServerGuard.Agent.Dto;

public sealed record Metrics(DateTime Time, List<SensorMetric> SensorMetrics);
public sealed record SensorMetric(string Name, List<Metric> Metrics);
public sealed record Metric(string Name, float? Value, MetricType Type);

public enum MetricType
{
    Voltage = 1,
    Current = 2,
    Power = 3,
    Clock = 4,
    Temperature = 5,
    Load = 6,
    Frequency = 7,
    Fan = 8,
    Flow = 9,
    Control = 10,
    Level = 11,
    Factor = 12,
    Data = 13,
    SmallData = 14,
    Throughput = 15,
    TimeSpan = 16,
    Energy = 17,
    Noise = 18
}
