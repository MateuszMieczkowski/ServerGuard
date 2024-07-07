namespace ServerGuard.Agent.Options;
internal sealed class OptionsFailedToLoadException : Exception
{
    public OptionsFailedToLoadException(string message) : base(message) { }
}
