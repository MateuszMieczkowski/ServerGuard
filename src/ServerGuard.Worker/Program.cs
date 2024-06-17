using Microsoft.Extensions.Hosting;
using Serilog;
using Microsoft.Extensions.Configuration;
using MassTransit;
using ServerGuard.Worker.Consumers;

var builder = Host.CreateApplicationBuilder();

builder.Services.AddSerilog((_, config) => config.ReadFrom.Configuration(builder.Configuration));

AddMessageBroker(builder);

var host = builder.Build();
await host.RunAsync();

static void AddMessageBroker(HostApplicationBuilder builder)
{
    var rabbitMqConnectionString = builder.Configuration.GetConnectionString("RabbitMq")!;

    builder.Services.AddMassTransit((config) =>
    {
        config.SetKebabCaseEndpointNameFormatter();
        config.UsingRabbitMq((context, cfg) =>
        {
            cfg.Host(new Uri(rabbitMqConnectionString));

            cfg.ConfigureEndpoints(context);
        });
        config.AddConsumer<MetricsCollectedConsumer>();
    });
}
