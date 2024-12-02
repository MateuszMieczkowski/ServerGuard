using Microsoft.Extensions.Hosting;
using Serilog;
using Quartz;
using ServerGuard.Agent.Jobs;
using Microsoft.Extensions.DependencyInjection;
using ServerGuard.Agent.Services;
using ServerGuard.Agent.Config;
using Refit;
using Microsoft.Extensions.Configuration;
using System.Text.Json.Serialization;
using ServerGuard.Agent.Dto;

HostApplicationBuilder builder = Host.CreateApplicationBuilder();

builder.Services.AddSerilog((_, config) =>
{
    config.ReadFrom.Configuration(builder.Configuration);
    if (builder.Configuration.GetValue<bool>("DebugMode"))
    {
        config.MinimumLevel.Debug();
        config.WriteTo.Console();
    }
    else
    {
        config.MinimumLevel.Information();
    }
});


if (!RootChecker.IsRoot())
{
    var errMsg = "Agent must be run as root";
    Log.Fatal(errMsg);
    return;
}

if (builder.Configuration.GetConnectionString("ApiUrl") is null || builder.Configuration.GetValue<string>("ApiKey") is null)
{
    var errMsg = "ApiUrl and ApiKey must be provided";
    Log.Fatal(errMsg);
    return;
}

builder.Services.AddMemoryCache();
AddQuartz(builder);
AddServices(builder);
AddIntegrationApi(builder);

var host = builder.Build();
await host.RunAsync();

static void AddIntegrationApi(HostApplicationBuilder builder)
{
    var serializerOptions = SystemTextJsonContentSerializer.GetDefaultJsonSerializerOptions();
    serializerOptions.NumberHandling = JsonNumberHandling.AllowNamedFloatingPointLiterals;
    serializerOptions.Converters.Add(new MetricTypeConverter());
    builder.Services
    .AddRefitClient<IIntegrationApi>(new RefitSettings()
    {
        ContentSerializer = new SystemTextJsonContentSerializer(serializerOptions),
    })
     .ConfigureHttpClient(c => c.BaseAddress = new Uri(builder.Configuration.GetConnectionString("ApiUrl")!));
}

static void AddQuartz(HostApplicationBuilder builder)
{
    builder.Services.AddQuartz(config =>
    {
        config.AddJob<CollectMetricsJob>(j => j.WithIdentity(nameof(CollectMetricsJob)));
        config.AddTrigger(t => t
            .ForJob(nameof(CollectMetricsJob))
            .WithIdentity($"{nameof(CollectMetricsJob)}Trigger")
            .WithSimpleSchedule(schedule => schedule.WithInterval(TimeSpan.FromSeconds(1)).RepeatForever()));
    });
    builder.Services.AddQuartzHostedService(config => config.WaitForJobsToComplete = true);
}

static void AddServices(HostApplicationBuilder builder)
{
    builder.Services.AddSingleton<IMetricCollector, MetricCollector>();
    builder.Services.AddSingleton<IAgentConfigProvider, ApiAgentConfigProvider>();
}