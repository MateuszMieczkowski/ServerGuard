using ServerGuard.Contracts.Dto;

namespace ServerGuard.Agent.Services;
internal interface IMetricCollector
{
    Metrics Collect();
}
