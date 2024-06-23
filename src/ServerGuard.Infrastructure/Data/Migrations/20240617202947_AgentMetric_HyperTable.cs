using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace ServerGuard.Infrastructure.Data.Migrations;

/// <inheritdoc />
public partial class AgentMetric_HyperTable : Migration
{
    /// <inheritdoc />
    protected override void Up(MigrationBuilder migrationBuilder)
    {
        migrationBuilder.Sql(@"
            SELECT create_hypertable('""AgentMetric""', 'Timestamp');
        ");
    }

    /// <inheritdoc />
    protected override void Down(MigrationBuilder migrationBuilder)
    {

    }
}
