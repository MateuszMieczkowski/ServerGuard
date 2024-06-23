using ServerGuard.Contracts.Dto;

namespace ServerGuard.Contracts.Events;

public sealed record MetricsCollected(Guid AgentId, Metrics Metrics);
