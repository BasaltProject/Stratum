package org.basaltmc.stratum.bukkit.legacy;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import org.bukkit.Material;
import org.bukkit.util.OldEnum;

/** Enum calls on a type that was an enum when the plugin was built and is a registry value now. */
public final class LegacyEnums {
    private static final ApiVersion kCurrent = new ApiVersion(Integer.MAX_VALUE, 0, 0);

    private LegacyEnums() {}

    public static String name(Object value) {
        if (value instanceof Material material) {
            return LegacyMaterials.name(material);
        }
        return value instanceof OldEnum<?> old ? old.name().replace('.', '_') : ((Enum<?>) value).name();
    }

    public static String toString(Object value) {
        if (value instanceof Material material) {
            return LegacyMaterials.name(material);
        }
        return value instanceof OldEnum<?> ? name(value) : value.toString();
    }

    public static String stringOf(Object value) {
        return value == null ? "null" : toString(value);
    }

    public static String objectsToString(Object value) {
        return stringOf(value);
    }

    public static String objectsToString(Object value, String fallback) {
        return value == null ? fallback : toString(value);
    }

    public static StringBuilder append(StringBuilder out, Object value) {
        return out.append(stringOf(value));
    }

    public static StringBuffer append(StringBuffer out, Object value) {
        return out.append(stringOf(value));
    }

    public static void println(PrintStream out, Object value) {
        out.println(stringOf(value));
    }

    public static void print(PrintStream out, Object value) {
        out.print(stringOf(value));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static int compareTo(Object value, Object other) {
        return ((Comparable) value).compareTo(other);
    }

    public static int ordinal(Object value) {
        return value instanceof OldEnum<?> old ? old.ordinal() : ((Enum<?>) value).ordinal();
    }

    private static Method staticMethod(Class<?> type, String name, Class<?>... parameters) {
        if (!OldEnum.class.isAssignableFrom(type)) {
            return null;
        }
        try {
            Method found = type.getMethod(name, parameters);
            return java.lang.reflect.Modifier.isStatic(found.getModifiers()) ? found : null;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static Object call(Method method, Object... arguments) {
        try {
            return method.invoke(null, arguments);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException runtime) {
                throw runtime;
            }
            throw new IllegalStateException(e.getCause());
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    /** Enum.valueOf, which also answers for a former enum by its old or current constant name. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object valueOf(Class<?> type, String name) {
        Objects.requireNonNull(name, "Name is null");
        if (type.isEnum()) {
            try {
                return Enum.valueOf((Class) type, name);
            } catch (IllegalArgumentException missing) {
                String renamed = LegacyRenames.renameText(kCurrent, type.getName().replace('.', '/'), name);
                if (renamed.equals(name)) {
                    throw missing;
                }
                return Enum.valueOf((Class) type, renamed);
            }
        }
        Method method = staticMethod(type, "valueOf", String.class);
        if (method == null) {
            throw new IllegalArgumentException(type.getName() + " is not an enum class");
        }
        return call(method, LegacyRenames.renameText(kCurrent, type.getName().replace('.', '/'), name));
    }

    /** Enum.valueOf for a plugin that sees Material through its LEGACY_ constants. */
    public static Object valueOfFlat(Class<?> type, String name) {
        return type == Material.class ? LegacyMaterials.valueOf(name) : valueOf(type, name);
    }

    public static boolean isEnum(Class<?> type) {
        return type.isEnum() || staticMethod(type, "values") != null;
    }

    public static Object[] enumConstants(Class<?> type) {
        if (type.isEnum()) {
            return type.getEnumConstants();
        }
        Method method = staticMethod(type, "values");
        return method == null ? null : (Object[]) call(method);
    }
}
