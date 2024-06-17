namespace ServerGuard.Contracts.Dto;

public sealed record MetricsCollection(DateTime Time, List<HardwareMetric> HardwareMetrics);
public sealed record HardwareMetric(string Name, List<Metric> Metrics);
public sealed record Metric(string Name, float? Value, string Type);
