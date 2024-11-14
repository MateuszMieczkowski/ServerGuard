using System.ComponentModel;
using System.Text.Json;
using System.Text.Json.Serialization;
using ServerGuard.Agent.Common;

namespace ServerGuard.Agent.Dto;

public sealed record Metrics(DateTime Time, List<SensorMetric> SensorMetrics);
public sealed record SensorMetric(string Name, List<Metric> Metrics);
public sealed record Metric(string Name, float? Value, MetricType Type);

public class MetricTypeConverter : JsonConverter<MetricType>
{
    public override MetricType Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
    {
        var value = reader.GetString();
        return Enum.GetValues<MetricType>().FirstOrDefault(e => e.GetDescription() == value);
    }

    public override void Write(Utf8JsonWriter writer, MetricType value, JsonSerializerOptions options)
    {
        writer.WriteStringValue(value.GetDescription());
    }
}


[JsonConverter(typeof(MetricTypeConverter))]
public enum MetricType
{
    [Description("VOLTAGE")]
    Voltage = 1,

    [Description("CURRENT")]
    Current = 2,

    [Description("POWER")]
    Power = 3,

    [Description("CLOCK")]
    Clock = 4,

    [Description("TEMPERATURE")]
    Temperature = 5,

    [Description("LOAD")]
    Load = 6,

    [Description("FREQUENCY")]
    Frequency = 7,

    [Description("FAN")]
    Fan = 8,

    [Description("FLOW")]
    Flow = 9,

    [Description("CONTROL")]
    Control = 10,

    [Description("LEVEL")]
    Level = 11,

    [Description("FACTOR")]
    Factor = 12,

    [Description("DATA")]
    Data = 13,

    [Description("SMALL_DATA")]
    SmallData = 14,

    [Description("THROUGHPUT")]
    Throughput = 15,

    [Description("TIMESPAN")]
    TimeSpan = 16,

    [Description("ENERGY")]
    Energy = 17,

    [Description("NOISE")]
    Noise = 18
}
