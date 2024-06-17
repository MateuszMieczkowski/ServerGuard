using LibreHardwareMonitor.Hardware;
using Microsoft.Extensions.Options;
using ServerGuard.Agent.Options;
using ServerGuard.Contracts.Dto;

namespace ServerGuard.Agent.Services;

internal sealed class MetricCollector : IMetricCollector
{
    private readonly AgentOptions _options;
    private readonly Computer _computer;
    public MetricCollector(IOptions<AgentOptions> options)
    {
        _options = options.Value;
        _computer = new Computer
        {
            IsCpuEnabled = _options.IsCpuEnabled,
            IsGpuEnabled = _options.IsGpuEnabled,
            IsMemoryEnabled = _options.IsMemoryEnabled,
            IsMotherboardEnabled = _options.IsMotherboardEnabled,
            IsControllerEnabled = _options.IsControllerEnabled,
            IsNetworkEnabled = _options.IsNetworkEnabled,
            IsStorageEnabled = _options.IsStorageEnabled
        };
    }

    public MetricsCollection Collect()
    {
        _computer.Open();
        _computer.Accept(new UpdateVisitor());

        List<HardwareMetric> hardwareMetrics = [];
        foreach (IHardware hardware in _computer.Hardware)
        {
            var metrics = new List<Metric>();
            foreach (ISensor sensor in hardware.Sensors)
            {
                metrics.Add(new Metric(sensor.Name, sensor.Value, sensor.SensorType.ToString()));
            }
            hardwareMetrics.Add(new HardwareMetric(hardware.Name, metrics));
        }
        _computer.Close();
        return new MetricsCollection(DateTime.UtcNow, hardwareMetrics);
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
