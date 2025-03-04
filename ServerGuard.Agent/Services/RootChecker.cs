using System.Runtime.InteropServices;
using System.Security.Principal;

namespace ServerGuard.Agent.Services;

static partial class RootChecker
{
    [LibraryImport("libc")]
    public static partial uint getuid();

    public static bool IsRoot()
    {
        if (RuntimeInformation.IsOSPlatform(OSPlatform.Windows))
        {
            bool isAdmin;
            using (var identity = WindowsIdentity.GetCurrent())
            {
                var principal = new WindowsPrincipal(identity);
                isAdmin = principal.IsInRole(WindowsBuiltInRole.Administrator);
            }

            return isAdmin;
        }
        else
        {
            return getuid() == 0;
        }
    }
}
