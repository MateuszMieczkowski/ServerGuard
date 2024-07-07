using System.Text.Json;

namespace ServerGuard.Agent.Options;
public class AgentConfig
{
    private const string ConfigPath = "agent.config.json";
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

    public void SaveToFile()
    {
        File.WriteAllText(ConfigPath, JsonSerializer.Serialize(this));
    }
    public void LoadFromFile()
    {
        if (!File.Exists(ConfigPath))
        {
            throw new OptionsFailedToLoadException("Failed to load agent options");
        }

        var json = File.ReadAllText(ConfigPath);
        var options = JsonSerializer.Deserialize<AgentConfig>(json)
            ?? throw new OptionsFailedToLoadException("Failed to load agent options");

        AgentId = options.AgentId;
        IsCpuEnabled = options.IsCpuEnabled;
        IsGpuEnabled = options.IsGpuEnabled;
        IsMemoryEnabled = options.IsMemoryEnabled;
        IsMotherboardEnabled = options.IsMotherboardEnabled;
        IsControllerEnabled = options.IsControllerEnabled;
        IsNetworkEnabled = options.IsNetworkEnabled;
        IsStorageEnabled = options.IsStorageEnabled;
        CollectEverySeconds = options.CollectEverySeconds;
        RabbitUsername = options.RabbitUsername;
        RabbitPassword = options.RabbitPassword;
        RabbitUrl = options.RabbitUrl;
        RabbitExchange = options.RabbitExchange;
        ApiKey = options.ApiKey;
    }

    internal void Update(AgentConfig newAgentOptions)
    {
        AgentId = newAgentOptions.AgentId;
        IsCpuEnabled = newAgentOptions.IsCpuEnabled;
        IsGpuEnabled = newAgentOptions.IsGpuEnabled;
        IsMemoryEnabled = newAgentOptions.IsMemoryEnabled;
        IsMotherboardEnabled = newAgentOptions.IsMotherboardEnabled;
        IsControllerEnabled = newAgentOptions.IsControllerEnabled;
        IsNetworkEnabled = newAgentOptions.IsNetworkEnabled;
        IsStorageEnabled = newAgentOptions.IsStorageEnabled;
        CollectEverySeconds = newAgentOptions.CollectEverySeconds;
        RabbitUsername = newAgentOptions.RabbitUsername;
        RabbitPassword = newAgentOptions.RabbitPassword;
        RabbitUrl = newAgentOptions.RabbitUrl;
        RabbitExchange = newAgentOptions.RabbitExchange;
        ApiKey = newAgentOptions.ApiKey;
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
