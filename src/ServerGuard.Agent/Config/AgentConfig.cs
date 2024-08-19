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
    public string RabbitUsername { get; private set; } = default!;
    public string RabbitPassword { get; private set; } = default!;
    public string RabbitUrl { get; private set; } = default!;
    public string RabbitExchange { get; private set; } = default!;
    public string ApiKey { get; private set; } = default!;

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
        string rabbitUsername,
        string rabbitPassword,
        string rabbitUrl,
        string rabbitExchange,
        string apiKey)
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
        RabbitUsername = rabbitUsername;
        RabbitPassword = rabbitPassword;
        RabbitUrl = rabbitUrl;
        RabbitExchange = rabbitExchange;
        ApiKey = apiKey;
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
                && RabbitUsername == options.RabbitUsername
                && RabbitPassword == options.RabbitPassword
                && RabbitUrl == options.RabbitUrl
                && RabbitExchange == options.RabbitExchange
                && ApiKey == options.ApiKey;
        }
        return base.Equals(obj);
    }

    public override int GetHashCode()
    {
        return base.GetHashCode();
    }
}
