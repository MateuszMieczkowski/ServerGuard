using System.ComponentModel;
using System.Reflection;

namespace ServerGuard.Agent.Common;

public static class EnumExtensions
{
    public static string GetDescription(this Enum value)
    {
        var field = value.GetType().GetField(value.ToString());
        var attribute = field?.GetCustomAttribute(typeof(DescriptionAttribute)) as DescriptionAttribute;
        if (attribute is null)
        {
            throw new ArgumentException("Enum value does not have a DescriptionAttribute");
        }
        return attribute.Description;
    }
}
