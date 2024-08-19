using Quartz;
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
        await Task.CompletedTask;
    }

}
