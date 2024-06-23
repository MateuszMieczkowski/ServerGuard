using Ardalis.SharedKernel;

namespace ServerGuard.Core.Models.AgentAggregate;

public class Agent : EntityBase<AgentId>, IAggregateRoot
{
    public string Name { get; private set; } = default!;

    private Agent() { }

    public Agent(string name)
    {
        Id = new AgentId(Guid.NewGuid());
        Name = name;
    }
}
