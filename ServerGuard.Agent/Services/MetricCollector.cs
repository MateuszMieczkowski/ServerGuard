using LibreHardwareMonitor.Hardware;
using ServerGuard.Agent.Config;
using ServerGuard.Agent.Dto;

namespace ServerGuard.Agent.Services;

internal interface IMetricCollector
{
    Task<Metrics> CollectAsync(CancellationToken cancellationToken = default);
}

internal sealed class MetricCollector : IMetricCollector
{
    private readonly IAgentConfigProvider _agentConfigProvider;
    private static readonly Computer _computer = _computer = new Computer
    {
        IsCpuEnabled = true,
        IsGpuEnabled = true,
        IsMemoryEnabled = true,
        IsMotherboardEnabled = true,
        IsControllerEnabled = true,
        IsNetworkEnabled = true,
        IsStorageEnabled = true
    };

    public MetricCollector(IAgentConfigProvider agentConfigProvider)
    {
        _agentConfigProvider = agentConfigProvider;
        _computer.Accept(new UpdateVisitor());
    }

    public async Task<Metrics> CollectAsync(CancellationToken cancellationToken = default)
    {
        await UpdateComputerAsync(cancellationToken);
        _computer.Open();
        _computer.Accept(new UpdateVisitor());

        List<SensorMetric> hardwareMetrics = [];
        foreach (IHardware hardware in _computer.Hardware)
        {
            var metrics = new List<Metric>();
            foreach (ISensor sensor in hardware.Sensors)
            {
                var metricType = Enum.Parse<MetricType>(sensor.SensorType.ToString());
                var value = sensor.Value;

                if (IsInvalidMetric(metricType, value))
                {
                    continue;
                }
                metrics.Add(new Metric(sensor.Name, sensor.Value, Enum.Parse<MetricType>(sensor.SensorType.ToString())));
            }
            hardwareMetrics.Add(new SensorMetric(hardware.Name, metrics));
        }
        _computer.Close();
        return new Metrics(DateTime.UtcNow, hardwareMetrics);
    }

    private static bool IsInvalidMetric(MetricType metricType, float? value)
    {
        return (metricType == MetricType.Load && (value < 0 || value > 100)) ||
                            (metricType == MetricType.Temperature && (value < -50 || value > 200)) ||
                            (metricType == MetricType.Voltage && (value < 0 || value > 15)) ||
                            (metricType == MetricType.Clock && (value < 0 || value > 6000)) ||
                            (metricType == MetricType.Fan && (value < 0 || value > 10000)) ||
                            (metricType == MetricType.Flow && (value < 0 || value > 100)) ||
                            (metricType == MetricType.Control && (value < 0 || value > 100)) ||
                            (metricType == MetricType.Level && (value < 0 || value > 100)) ||
                            (metricType == MetricType.Factor && (value < 0 || value > 10)) ||
                            (metricType == MetricType.Power && (value < 0 || value > 1000)) ||
                            (metricType == MetricType.Data && (value < 0)) ||
                            (metricType == MetricType.SmallData && (value < 0)) ||
                            (metricType == MetricType.Throughput && (value < 0)) ||
                            (metricType == MetricType.Current && (value < 0)) ||
                            (metricType == MetricType.Frequency && (value < 0)) ||
                            (metricType == MetricType.TimeSpan && (value < 0)) ||
                            (metricType == MetricType.Energy && (value < 0)) ||
                            (metricType == MetricType.Noise && (value < 0));
    }

    private async Task UpdateComputerAsync(CancellationToken cancellationToken)
    {
        AgentConfig agentConfig = await _agentConfigProvider.GetAsync(cancellationToken);
        _computer.IsCpuEnabled = agentConfig.IsCpuEnabled;
        _computer.IsGpuEnabled = agentConfig.IsGpuEnabled;
        _computer.IsMemoryEnabled = agentConfig.IsMemoryEnabled;
        _computer.IsMotherboardEnabled = agentConfig.IsMotherboardEnabled;
        _computer.IsControllerEnabled = agentConfig.IsControllerEnabled;
        _computer.IsNetworkEnabled = agentConfig.IsNetworkEnabled;
        _computer.IsStorageEnabled = agentConfig.IsStorageEnabled;
    }
}

internal sealed class UpdateVisitor : IVisitor
{
    public void VisitComputer(IComputer computer)
    {
        computer.Traverse(this);
    }
    public void VisitHardware(IHardware hardware)
    {
        hardware.Update();
        foreach (IHardware subHardware in hardware.SubHardware)
        {
            subHardware.Accept(this);
        }
    }
    public void VisitSensor(ISensor sensor) { }
    public void VisitParameter(IParameter parameter) { }
}
