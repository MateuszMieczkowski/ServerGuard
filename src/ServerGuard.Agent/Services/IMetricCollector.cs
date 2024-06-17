using ServerGuard.Contracts.Dto;

namespace ServerGuard.Agent.Services;
internal interface IMetricCollector
{
    MetricsCollection Collect();
}
