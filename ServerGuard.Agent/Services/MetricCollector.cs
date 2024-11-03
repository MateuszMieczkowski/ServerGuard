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
                metrics.Add(new Metric(sensor.Name, sensor.Value, Enum.Parse<MetricType>(sensor.SensorType.ToString())));
            }
            hardwareMetrics.Add(new SensorMetric(hardware.Name, metrics));
        }
        _computer.Close();
        return new Metrics(DateTime.UtcNow, hardwareMetrics);
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
