using Quartz;
using ServerGuard.Core.Models.AgentAggregate;
using ServerGuard.Infrastructure.Data;

namespace ServerGuard.Infrastructure.Jobs;

internal sealed class SeedJob : IJob
{
    private readonly AppDbContext _dbContext;

    public SeedJob(AppDbContext dbContext)
    {
        _dbContext = dbContext;
    }

    public async Task Execute(IJobExecutionContext context)
    {
        await AddAgents(context.CancellationToken);
    }

    private async Task AddAgents(CancellationToken cancellationToken)
    {
        if (_dbContext.Agent.Any())
        {
            return;
        }
        var agents = new List<Agent>
        {
           new Agent("Agent 1")
        };
        _dbContext.AddRange(agents);
        await _dbContext.SaveChangesAsync(cancellationToken);
    }
}
