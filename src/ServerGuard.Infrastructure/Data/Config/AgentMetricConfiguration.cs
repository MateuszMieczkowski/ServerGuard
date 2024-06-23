using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;
using ServerGuard.Core.Models.AgentAggregate;
using ServerGuard.Core.Models.AgentMetricAggregate;

namespace ServerGuard.Infrastructure.Data.Config;

public sealed class AgentMetricConfiguration : IEntityTypeConfiguration<AgentMetric>
{
    public void Configure(EntityTypeBuilder<AgentMetric> builder)
    {
        builder.ToTable("AgentMetric");

        builder.HasKey(x =>  new { x.AgentId, x.SensorName, x.Name, x.Timestamp, x.Type, x.Value });

        builder.Property(x => x.AgentId)
            .HasConversion(id => id.Value, value => new AgentId(value))
            .IsRequired();

        builder.Property(x => x.SensorName)
            .IsRequired();

        builder.Property(x => x.Name)
            .IsRequired();

        builder.Property(x => x.Type)
            .IsRequired();

        builder.Property(x => x.Timestamp)
            .IsRequired();
    }
}
