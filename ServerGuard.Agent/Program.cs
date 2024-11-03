using Microsoft.Extensions.Hosting;
using Serilog;
using Quartz;
using ServerGuard.Agent.Jobs;
using Microsoft.Extensions.DependencyInjection;
using ServerGuard.Agent.Services;
using ServerGuard.Agent.Config;
using Refit;

var builder = Host.CreateApplicationBuilder();

builder.Services.AddSerilog((_, config) => config.ReadFrom.Configuration(builder.Configuration));

if (!RootChecker.IsRoot())
{
    var errMsg = "Agent must be run as root";
    Log.Fatal(errMsg);
    return;
}
builder.Services.AddMemoryCache();
AgentConfig agentConfig = await AddAgentConfigAsync(builder);
AddQuartz(builder, agentConfig);
AddServices(builder);
AddIntegrationApi(builder, agentConfig);

var host = builder.Build();
await host.RunAsync();

static void AddIntegrationApi(HostApplicationBuilder builder, AgentConfig agentConfig)
{
    builder.Services
     .AddRefitClient<IIntegrationApi>()
     .ConfigureHttpClient(c => c.BaseAddress = new Uri(agentConfig.ApiUrl));

}

static void AddQuartz(HostApplicationBuilder builder, AgentConfig agentConfig)
{
    builder.Services.AddQuartz(config =>
    {
        config.AddJob<CollectMetricsJob>(j => j.WithIdentity(nameof(CollectMetricsJob)));
        config.AddTrigger(t => t
            .ForJob(nameof(CollectMetricsJob))
            .WithIdentity($"{nameof(CollectMetricsJob)}Trigger")
            .WithSimpleSchedule(schedule => schedule.WithInterval(TimeSpan.FromSeconds(agentConfig.CollectEverySeconds)).RepeatForever()));
    });
    builder.Services.AddQuartzHostedService(config => config.WaitForJobsToComplete = true);
}

static async Task<AgentConfig> AddAgentConfigAsync(HostApplicationBuilder builder)
{
    builder.Services.Configure<AgentConfigOptions>(builder.Configuration.GetSection("AgentConfig"));
    builder.Services.AddSingleton<IAgentConfigProvider, FileAgentConfigProvider>();
    var agentConfig = await builder.Services
        .BuildServiceProvider()
        .GetRequiredService<IAgentConfigProvider>()
        .GetAsync(default);

    return agentConfig;
}

static void AddServices(HostApplicationBuilder builder)
{
    builder.Services.AddSingleton<IMetricCollector, MetricCollector>();
}
