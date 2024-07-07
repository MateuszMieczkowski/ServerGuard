using Microsoft.Extensions.Hosting;
using Serilog;
using Microsoft.Extensions.Configuration;
using MassTransit;
using ServerGuard.Worker.Consumers;
using Microsoft.Extensions.DependencyInjection;
using ServerGuard.Worker.Repository;
using Microsoft.Azure.Cosmos;

var builder = Host.CreateApplicationBuilder();

builder.Services.AddSerilog((_, config) => config.ReadFrom.Configuration(builder.Configuration));

if (builder.Configuration.GetValue<bool>("UseCosmos"))
{
    var cosmosClientOptions = new CosmosClientOptions
    {
        AllowBulkExecution = true
    };
    builder.Services.AddSingleton(new CosmosClient(builder.Configuration.GetConnectionString("Cosmos"), cosmosClientOptions));
    builder.Services.AddScoped<IMetricRepository, CosmosMetricRepository>();
}
else
{
    builder.Services.AddScoped<IMetricRepository, PostgresMetricRepository>();
}

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
