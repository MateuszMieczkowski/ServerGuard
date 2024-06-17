namespace ServerGuard.Worker.Options;
internal sealed class MessageBrokerOptions
{
    public string RabbitUsername { get; set; } = default!;
    public string RabbitPassword { get; set; } = default!;
    public string RabbitUrl { get; set; } = default!;
}
