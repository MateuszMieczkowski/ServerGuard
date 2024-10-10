using System.Text.Json.Serialization;

namespace ServerGuard.Agent.Config;
public class AgentConfig
{
    public Guid AgentId { get; private set; }
    public bool IsCpuEnabled { get; private set; }
    public bool IsGpuEnabled { get; private set; }
    public bool IsMemoryEnabled { get; private set; }
    public bool IsMotherboardEnabled { get; private set; }
    public bool IsControllerEnabled { get; private set; }
    public bool IsNetworkEnabled { get; private set; }
    public bool IsStorageEnabled { get; private set; }
    public int CollectEverySeconds { get; private set; }
    public string ApiKey { get; private set; } = default!;
    public string ApiUrl { get; private set; } = default!;

    public AgentConfig()
    {
    }

    [JsonConstructor]
    public AgentConfig(
        Guid agentId,
        bool isCpuEnabled,
        bool isGpuEnabled,
        bool isMemoryEnabled,
        bool isMotherboardEnabled,
        bool isControllerEnabled,
        bool isNetworkEnabled,
        bool isStorageEnabled,
        int collectEverySeconds,
        string apiKey,
        string apiUrl)
    {
        AgentId = agentId;
        IsCpuEnabled = isCpuEnabled;
        IsGpuEnabled = isGpuEnabled;
        IsMemoryEnabled = isMemoryEnabled;
        IsMotherboardEnabled = isMotherboardEnabled;
        IsControllerEnabled = isControllerEnabled;
        IsNetworkEnabled = isNetworkEnabled;
        IsStorageEnabled = isStorageEnabled;
        CollectEverySeconds = collectEverySeconds;
        ApiKey = apiKey;
        ApiUrl = apiUrl;
    }

    public override bool Equals(object? obj)
    {
        if (obj is AgentConfig options)
        {
            return AgentId == options.AgentId
                && IsCpuEnabled == options.IsCpuEnabled
                && IsGpuEnabled == options.IsGpuEnabled
                && IsMemoryEnabled == options.IsMemoryEnabled
                && IsMotherboardEnabled == options.IsMotherboardEnabled
                && IsControllerEnabled == options.IsControllerEnabled
                && IsNetworkEnabled == options.IsNetworkEnabled
                && IsStorageEnabled == options.IsStorageEnabled
                && CollectEverySeconds == options.CollectEverySeconds
                && ApiKey == options.ApiKey
                && ApiUrl == options.ApiUrl;
        }
        return base.Equals(obj);
    }

    public override int GetHashCode()
    {
        return base.GetHashCode();
    }
}
