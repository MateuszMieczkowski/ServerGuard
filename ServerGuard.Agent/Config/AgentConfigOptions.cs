namespace ServerGuard.Agent.Config;
internal sealed class AgentConfigOptions
{
    public string Path { get; set; } = default!;
    public string ApiEndpointUrl { get; set; } = default!;
    public int CacheDurationMinutes { get; set; }
}
