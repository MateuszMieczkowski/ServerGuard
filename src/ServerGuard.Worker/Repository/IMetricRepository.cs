using ServerGuard.Contracts.Dto;

namespace ServerGuard.Worker.Repository;
internal interface IMetricRepository
{
    Task InsertAsync(Guid agentId, Metrics metrics, CancellationToken cancellationToken = default);
}
