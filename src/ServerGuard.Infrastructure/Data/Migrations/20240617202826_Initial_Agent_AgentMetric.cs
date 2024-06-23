using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace ServerGuard.Infrastructure.Data.Migrations;

/// <inheritdoc />
public partial class Initial_Agent_AgentMetric : Migration
{
    /// <inheritdoc />
    protected override void Up(MigrationBuilder migrationBuilder)
    {
        migrationBuilder.CreateTable(
            name: "Agent",
            columns: table => new
            {
                Id = table.Column<Guid>(type: "uuid", nullable: false),
                Name = table.Column<string>(type: "text", nullable: false)
            },
            constraints: table =>
            {
                table.PrimaryKey("PK_Agent", x => x.Id);
            });

        migrationBuilder.CreateTable(
            name: "AgentMetric",
            columns: table => new
            {
                AgentId = table.Column<Guid>(type: "uuid", nullable: false),
                SensorName = table.Column<string>(type: "text", nullable: false),
                Name = table.Column<string>(type: "text", nullable: false),
                Timestamp = table.Column<DateTime>(type: "timestamp with time zone", nullable: false),
                Value = table.Column<float>(type: "real", nullable: true),
                Type = table.Column<int>(type: "integer", nullable: false)
            },
            constraints: table =>
            {
                table.PrimaryKey("PK_AgentMetric", x => new { x.AgentId, x.SensorName, x.Name, x.Timestamp, x.Type, x.Value });
                table.ForeignKey("FK_AgentMetric_Agent_AgentId", x => x.AgentId, "Agent", "Id", onDelete: ReferentialAction.Cascade);
            });
    }

    /// <inheritdoc />
    protected override void Down(MigrationBuilder migrationBuilder)
    {
        migrationBuilder.DropTable(
            name: "Agent");

        migrationBuilder.DropTable(
            name: "AgentMetric");
    }
}
