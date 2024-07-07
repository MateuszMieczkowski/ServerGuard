using LibreHardwareMonitor.Hardware;
using ServerGuard.Agent.Options;
using ServerGuard.Contracts.Dto;

namespace ServerGuard.Agent.Services;

internal sealed class MetricCollector : IMetricCollector
{
    private readonly AgentConfig _agentConfig;
    private readonly Computer _computer;
    public MetricCollector(AgentConfig agentConfig)
    {
        _agentConfig = agentConfig;
        _computer = new Computer
        {
            IsCpuEnabled = _agentConfig.IsCpuEnabled,
            IsGpuEnabled = _agentConfig.IsGpuEnabled,
            IsMemoryEnabled = _agentConfig.IsMemoryEnabled,
            IsMotherboardEnabled = _agentConfig.IsMotherboardEnabled,
            IsControllerEnabled = _agentConfig.IsControllerEnabled,
            IsNetworkEnabled = _agentConfig.IsNetworkEnabled,
            IsStorageEnabled = _agentConfig.IsStorageEnabled
        };
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
