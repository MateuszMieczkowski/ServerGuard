using Microsoft.Extensions.Caching.Memory;
using Microsoft.Extensions.Options;
using ServerGuard.Agent.Common;
using ServerGuard.Agent.Options;
using System.Text.Json;

namespace ServerGuard.Agent.Config;

internal sealed class FileAgentConfigProvider : IAgentConfigProvider
{
    private static readonly SemaphoreSlim Semaphore = new(1);

    private readonly AgentConfigOptions _options;
    private readonly IMemoryCache _cache;

    public FileAgentConfigProvider(IOptions<AgentConfigOptions> options, IMemoryCache cache)
    {
        _options = options.Value;
        _cache = cache;
    }

    public async Task<AgentConfig> GetAsync(CancellationToken cancellationToken)
    {
        await Semaphore.WaitAsync(cancellationToken);

        if (_cache.TryGetValue<AgentConfig>(Constants.AgentConfigCacheKey, out var cachedConfig))
        {
            Semaphore.Release();
            return cachedConfig!;
        }
        var config = await LoadFromFileAsync(cancellationToken);
        _cache.Set(Constants.AgentConfigCacheKey, config, TimeSpan.FromMinutes(_options.CacheDurationMinutes));

        Semaphore.Release();
        return config;
    }

    private async Task<AgentConfig> LoadFromFileAsync(CancellationToken cancellationToken)
    {
        if (!File.Exists(_options.Path))
        {
            throw new OptionsFailedToLoadException("Failed to load agent options");
        }
        var json = await File.ReadAllTextAsync(_options.Path, cancellationToken);
        var config = JsonSerializer.Deserialize<AgentConfig>(json)
            ?? throw new OptionsFailedToLoadException("Failed to load agent options");

        return config;
    }
}
