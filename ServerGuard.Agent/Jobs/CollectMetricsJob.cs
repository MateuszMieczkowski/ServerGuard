using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using ServerGuard.Agent.Config;
using ServerGuard.Agent.Services;

namespace ServerGuard.Agent.Jobs;

internal sealed class CollectMetricsJob : BackgroundService
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

    protected override async Task ExecuteAsync(CancellationToken cancellationToken)
    {
        while (!cancellationToken.IsCancellationRequested)
        {
            _logger.LogInformation("Collecting metrics...");
            var agentConfig = await _agentConfigProvider.GetAsync(cancellationToken);
            var metrics = await _metricCollector.CollectAsync();
            await _integrationApi.SendMetrics(_configuration.GetValue<string>("ApiKey")!, agentConfig.ResourceGroupId, agentConfig.AgentId, metrics, cancellationToken);
            _logger.LogInformation("Metrics collected");
            await Task.Delay(TimeSpan.FromSeconds(agentConfig.CollectEverySeconds), cancellationToken);
        }
    }
}
