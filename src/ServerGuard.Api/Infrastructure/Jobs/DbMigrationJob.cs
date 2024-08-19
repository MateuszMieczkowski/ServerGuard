using Microsoft.EntityFrameworkCore;
using Quartz;
using ServerGuard.Infrastructure.Data;

namespace ServerGuard.Infrastructure.Jobs;

internal sealed class DbMigrationJob : IJob
{
    private readonly AppDbContext _dbContext;
    private readonly ILogger<DbMigrationJob> _logger;

    public DbMigrationJob(AppDbContext dbContext, ILogger<DbMigrationJob> logger)
    {
        _dbContext = dbContext;
        _logger = logger;
    }

    public async Task Execute(IJobExecutionContext context)
    {
        _logger.LogInformation("Migrating database...");
        await _dbContext.Database.MigrateAsync(context.CancellationToken);
        _logger.LogInformation("Database migrated");
    }
}
