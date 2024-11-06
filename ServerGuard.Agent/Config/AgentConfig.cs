using System.Text.Json.Serialization;

namespace ServerGuard.Agent.Config;
public class AgentConfig
{
    public Guid ResourceGroupId { get; set; }
    public Guid AgentId { get; set; }
    public bool IsCpuEnabled { get; set; }
    public bool IsGpuEnabled { get; set; }
    public bool IsMemoryEnabled { get; set; }
    public bool IsMotherboardEnabled { get; set; }
    public bool IsControllerEnabled { get; set; }
    public bool IsNetworkEnabled { get; set; }
    public bool IsStorageEnabled { get; set; }
    public int CollectEverySeconds { get; set; }
}
