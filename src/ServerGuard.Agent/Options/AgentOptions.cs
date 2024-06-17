namespace ServerGuard.Agent.Options;
public class AgentOptions
{
    public string AgentId { get; set; } = default!; 
    public bool IsCpuEnabled { get; set; }
    public bool IsGpuEnabled { get; set; }
    public bool IsMemoryEnabled { get; set; }
    public bool IsMotherboardEnabled { get; set; }
    public bool IsControllerEnabled { get; set; }
    public bool IsNetworkEnabled { get; set; }
    public bool IsStorageEnabled { get; set; }
    public int CollectEverySeconds { get; set; }
    public string RabbitUsername { get; set; } = default!;
    public string RabbitPassword { get; set; } = default!;
    public string RabbitUrl { get; set; } = default!;
    public string RabbitExchange { get; set; } = default!;
    public string ApiKey { get; set; } = default!;
}
