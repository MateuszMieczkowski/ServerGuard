using Quartz;

namespace ServerGuard.Agent.Config;

internal interface IAgentConfigProvider
{
    Task<AgentConfig> GetAsync(CancellationToken cancellationToken);
}
