using System.Reflection;
using ServerGuard.Infrastructure;
using Serilog;
using Serilog.Extensions.Logging;

var logger = Log.Logger = new LoggerConfiguration()
  .Enrich.FromLogContext()
  .WriteTo.Console()
  .CreateLogger();

logger.Information("Starting web host");

var builder = WebApplication.CreateBuilder(args);

builder.Host.UseSerilog((_, config) => config.ReadFrom.Configuration(builder.Configuration));
var microsoftLogger = new SerilogLoggerFactory(logger)
    .CreateLogger<Program>();

ConfigureMediatR();

builder.Services.AddInfrastructureServices(builder.Configuration, microsoftLogger);

if (builder.Environment.IsDevelopment())
{
    builder.Services.AddDbMigrationJob();
    builder.Services.AddSeedDbJob();
}
else
{

}

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.UseDeveloperExceptionPage();
}
else
{
    app.UseHsts();
}

app.UseHttpsRedirection();

SeedDatabase(app);

app.Run();

static void SeedDatabase(WebApplication app)
{
    //using var scope = app.Services.CreateScope();
    //var services = scope.ServiceProvider;

    //try
    //{
    //  var context = services.GetRequiredService<AppDbContext>();
    //  //          context.Database.Migrate();
    //  context.Database.EnsureCreated();
    //  SeedData.Initialize(services);
    //}
    //catch (Exception ex)
    //{
    //  var logger = services.GetRequiredService<ILogger<Program>>();
    //  logger.LogError(ex, "An error occurred seeding the DB. {exceptionMessage}", ex.Message);
    //}
}

void ConfigureMediatR()
{
    //TODO: Add assemblies to scan for handlers
    var mediatRAssemblies = new Assembly[]
    {
        Assembly.GetExecutingAssembly()
    };
    builder.Services.AddMediatR(cfg => cfg.RegisterServicesFromAssemblies(mediatRAssemblies!));
}

// Make the implicit Program.cs class public, so integration tests can reference the correct assembly for host building
public partial class Program
{
}
