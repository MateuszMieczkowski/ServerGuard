using Microsoft.Extensions.Logging;
using Quartz;

namespace ServerGuard.Agent.Jobs;

[DisallowConcurrentExecution]
internal sealed class GetConfigJob : IJob
{
    private readonly ILogger<GetConfigJob> _logger;

    public GetConfigJob(ILogger<GetConfigJob> logger)
    {
        _logger = logger;
    }

    public Task Execute(IJobExecutionContext context)
    {
        _logger.LogInformation("{job} started.", nameof(GetConfigJob));

        _logger.LogInformation("{job} finished.", nameof(GetConfigJob));

        return Task.CompletedTask;
    }
}
