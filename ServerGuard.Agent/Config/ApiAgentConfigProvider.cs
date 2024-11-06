
using Microsoft.Extensions.Caching.Memory;
using Microsoft.Extensions.Configuration;
using Quartz;
using ServerGuard.Agent.Common;
using ServerGuard.Agent.Config;
using ServerGuard.Agent.Jobs;
using ServerGuard.Agent.Services;

internal class ApiAgentConfigProvider : IAgentConfigProvider
{
    private readonly IIntegrationApi _integrationApi;
    private readonly IConfiguration _configuration;

    private readonly IMemoryCache _cache;

    public ApiAgentConfigProvider(
        IIntegrationApi integrationApi,
        IConfiguration configuration,
        IMemoryCache cache)
    {
        _integrationApi = integrationApi;
        _configuration = configuration;
        _cache = cache;
    }

    public async Task<AgentConfig> GetAsync(CancellationToken cancellationToken = default)
    {
        if (_cache.TryGetValue<AgentConfig>(Constants.AgentConfigCacheKey, out var cachedConfig))
        {
            return cachedConfig!;
        }
        var config = await _integrationApi.GetAgentConfigAsync(_configuration.GetValue<string>("ApiKey"), cancellationToken);
        _cache.Set(Constants.AgentConfigCacheKey, config, TimeSpan.FromMinutes(1));


        return config;
    }

}