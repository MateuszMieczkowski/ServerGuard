using Ardalis.Result;
using Ardalis.SharedKernel;

namespace ServerGuard.UseCases.Contributors.Get;

public record GetContributorQuery(int ContributorId) : IQuery<Result<ContributorDTO>>;
