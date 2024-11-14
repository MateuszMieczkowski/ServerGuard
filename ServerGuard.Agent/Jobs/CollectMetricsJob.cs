using System.Text.Json;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;
using Quartz;
using ServerGuard.Agent.Config;
using ServerGuard.Agent.Services;

namespace ServerGuard.Agent.Jobs;

[DisallowConcurrentExecution]
internal sealed class CollectMetricsJob : IJob
{
    private readonly ILogger<CollectMetricsJob> _logger;
    private readonly IMetricCollector _metricCollector;
    private readonly IAgentConfigProvider _agentConfigProvider;
    private readonly IIntegrationApi _integrationApi;
    private readonly IConfiguration _configuration;
    public CollectMetricsJob(
        ILogger<CollectMetricsJob> logger,
        IMetricCollector metricCollector,
        IAgentConfigProvider agentConfigProvider,
        IIntegrationApi integrationApi,
        IConfiguration configuration)
    {
        _logger = logger;
        _metricCollector = metricCollector;
        _agentConfigProvider = agentConfigProvider;
        _integrationApi = integrationApi;
        _configuration = configuration;
    }

    public async Task Execute(IJobExecutionContext context)
    {
        _logger.LogInformation("Collecting metrics...");
        var agentConfig = await _agentConfigProvider.GetAsync(context.CancellationToken);
        var metrics = await _metricCollector.CollectAsync();
        if (_configuration.GetValue<bool>("DebugMode"))
        {
            _logger.LogInformation("Metrics: {Metrics}", JsonSerializer.Serialize(metrics, new JsonSerializerOptions
            {
                NumberHandling = System.Text.Json.Serialization.JsonNumberHandling.AllowNamedFloatingPointLiterals
            }));
        }
        await _integrationApi.SendMetrics(_configuration.GetValue<string>("ApiKey"), agentConfig.ResourceGroupId, agentConfig.AgentId, metrics, context.CancellationToken);
        _logger.LogInformation("Metrics collected");
        await Task.Delay(TimeSpan.FromSeconds(agentConfig.CollectEverySeconds), context.CancellationToken);
    }
}
