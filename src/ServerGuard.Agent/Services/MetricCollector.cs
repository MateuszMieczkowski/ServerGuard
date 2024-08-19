using LibreHardwareMonitor.Hardware;
using ServerGuard.Agent.Config;
using ServerGuard.Contracts.Dto;

namespace ServerGuard.Agent.Services;

internal interface IMetricCollector
{
    Metrics Collect();
}

internal sealed class MetricCollector : IMetricCollector
{
    private readonly IAgentConfigProvider _agentConfigProvider;
    private readonly Computer _computer;
    public MetricCollector(IAgentConfigProvider agentConfigProvider)
    {
        var agentConfig = agentConfigProvider.GetAsync(default)
            .GetAwaiter()
            .GetResult();

        _computer = new Computer
        {
            IsCpuEnabled = agentConfig.IsCpuEnabled,
            IsGpuEnabled = agentConfig.IsGpuEnabled,
            IsMemoryEnabled = agentConfig.IsMemoryEnabled,
            IsMotherboardEnabled = agentConfig.IsMotherboardEnabled,
            IsControllerEnabled = agentConfig.IsControllerEnabled,
            IsNetworkEnabled = agentConfig.IsNetworkEnabled,
            IsStorageEnabled = agentConfig.IsStorageEnabled
        };
        _agentConfigProvider = agentConfigProvider;
    }

    public Metrics Collect()
    {
        _computer.Open();
        _computer.Accept(new UpdateVisitor());

        List<SensorMetric> hardwareMetrics = [];
        foreach (IHardware hardware in _computer.Hardware)
        {
            var metrics = new List<Metric>();
            foreach (ISensor sensor in hardware.Sensors)
            {
                metrics.Add(new Metric(sensor.Name, sensor.Value, Enum.Parse<MetricType>(sensor.SensorType.ToString())));
            }
            hardwareMetrics.Add(new SensorMetric(hardware.Name, metrics));
        }
        _computer.Close();
        return new Metrics(DateTime.UtcNow, hardwareMetrics);
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
