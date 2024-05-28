using Ardalis.Result;
using Ardalis.SharedKernel;

namespace ServerGuard.UseCases.Contributors.Delete;

public record DeleteContributorCommand(int ContributorId) : ICommand<Result>;
