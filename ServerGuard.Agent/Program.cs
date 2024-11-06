using Microsoft.Extensions.Hosting;
using Serilog;
using Quartz;
using ServerGuard.Agent.Jobs;
using Microsoft.Extensions.DependencyInjection;
using ServerGuard.Agent.Services;
using ServerGuard.Agent.Config;
using Refit;
using Microsoft.Extensions.Configuration;
using System.Net;
using System.Text.Json;
using System.Net.Http.Json;

var builder = Host.CreateApplicationBuilder();

builder.Services.AddSerilog((_, config) => config.ReadFrom.Configuration(builder.Configuration));

// if (!RootChecker.IsRoot())
// {
//     var errMsg = "Agent must be run as root";
//     Log.Fatal(errMsg);
//     return;
// }
// var httpClient = new HttpClient();
// var baseApiUrl = builder.Configuration.GetConnectionString("ApiUrl");
// var httpRequest = new HttpRequestMessage(HttpMethod.Get, baseApiUrl + "/api/agentConfig");
// httpRequest.Headers.Add("x-api-key", builder.Configuration.GetValue<string>("ApiKey"));
// HttpResponseMessage response = await httpClient.SendAsync(httpRequest);
// if (response.StatusCode != HttpStatusCode.OK)
// {
//     Log.Fatal("Failed to get agent config: {response}", response);
//     return;
// }
// var agentConfig = await response.Content.ReadFromJsonAsync<AgentConfig>();

builder.Services.AddMemoryCache();
AddQuartz(builder);
AddServices(builder);
AddIntegrationApi(builder);

var host = builder.Build();
await host.RunAsync();

static void AddIntegrationApi(HostApplicationBuilder builder)
{
    builder.Services
     .AddRefitClient<IIntegrationApi>()
     .ConfigureHttpClient(c => c.BaseAddress = new Uri(builder.Configuration.GetConnectionString("ApiUrl")));
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
