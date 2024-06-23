using Ardalis.GuardClauses;
using ServerGuard.Infrastructure.Data;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using Quartz;
using ServerGuard.Infrastructure.Jobs;

namespace ServerGuard.Infrastructure;
public static class InfrastructureServiceExtensions
{
    public static IServiceCollection AddInfrastructureServices(
      this IServiceCollection services,
      ConfigurationManager config,
      ILogger logger)
    {
        string? connectionString = config.GetConnectionString("DefaultConnection");
        Guard.Against.Null(connectionString);
        services.AddDbContext<AppDbContext>(options => options.UseNpgsql(connectionString));
        AddQuartz(services);

        logger.LogInformation("{Project} services registered", "Infrastructure");
        return services;
    }

    public static IServiceCollection AddDbMigrationJob(this IServiceCollection services)
    {
        services.AddQuartz(config =>
        {
            config.AddJob<DbMigrationJob>(j => j.WithIdentity("DbMigrationJob"));
            config.AddTrigger(t => t
                .ForJob("DbMigrationJob")
                .WithIdentity("DbMigrationJobTrigger")
                .StartNow());
        });

        return services;
    }

    private static void AddQuartz(IServiceCollection services)
    {
        services.AddQuartz();
        services.AddQuartzHostedService(config => config.WaitForJobsToComplete = true);
    }

}
