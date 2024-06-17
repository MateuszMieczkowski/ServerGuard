using Microsoft.Extensions.Hosting;
using Serilog;
using Quartz;
using ServerGuard.Agent.Jobs;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using ServerGuard.Agent.Options;
using ServerGuard.Agent.Services;
using MassTransit;

var builder = Host.CreateApplicationBuilder();

builder.Services.AddSerilog((_, config) => config.ReadFrom.Configuration(builder.Configuration));

AddAgentOptions(builder);
AddQuartz(builder);
AddServices(builder);
AddMessageBroker(builder);

var host = builder.Build();
await host.RunAsync();

static void AddMessageBroker(HostApplicationBuilder builder)
{
    var agentOptions = builder.Configuration.GetSection("AgentOptions").Get<AgentOptions>()!;
    builder.Services.AddMassTransit((config) =>
    {
        config.SetKebabCaseEndpointNameFormatter();
        config.UsingRabbitMq((context, cfg) =>
        {
            cfg.Host(agentOptions.RabbitUrl, "/", cfg =>
            {
                cfg.Username(agentOptions.RabbitUsername);
                cfg.Password(agentOptions.RabbitPassword);
                cfg.ConnectionName(agentOptions.AgentId);
                //TODO: Add a custom connection factory
                //cfg.OnRefreshConnectionFactory((_, factory) =>
                //{
                //    factory.HostName = agentOptions.RabbitUrl;
                //    factory.UserName = agentOptions.RabbitUsername;
                //    factory.Password = agentOptions.RabbitPassword;
                //});
            });

            //cfg.Publish<MetricsCollectedEvent>(x =>
            //{
            //    x.Durable = true;
            //    x.ExchangeType = ExchangeType.Topic;
            //    x.BindQueue(agentOptions.RabbitExchange, agentOptions.Qiu);
            //});
            cfg.ConfigureEndpoints(context);
        });
    });
}

static void AddQuartz(HostApplicationBuilder builder)
{
    builder.Services.AddQuartz(config =>
    {
        config.AddJob<CollectMetricsJob>(j => j.WithIdentity("CollectMetricsJob"));
        config.AddTrigger(t => t
            .ForJob("CollectMetricsJob")
            .WithIdentity("CollectMetricsJobTrigger")
            .WithSimpleSchedule(schedule => schedule.WithInterval(TimeSpan.FromSeconds(10)).RepeatForever()));
    });
    builder.Services.AddQuartzHostedService(config => config.WaitForJobsToComplete = true);
}

static void AddAgentOptions(HostApplicationBuilder builder)
{
    builder.Configuration.AddJsonFile("agent.config.json", optional: false);
    builder.Services.Configure<AgentOptions>(builder.Configuration.GetSection("AgentOptions"));
}

static void AddServices(HostApplicationBuilder builder)
{
    builder.Services.AddSingleton<IMetricCollector, MetricCollector>();
}
