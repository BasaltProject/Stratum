package org.basaltmc.stratum.bukkit.legacy;

import org.bukkit.Material;
import org.bukkit.util.OldEnum;

/** Enum calls on a type that was an enum when the plugin was built and is a registry value now. */
public final class LegacyEnums {
    private LegacyEnums() {}

    public static String name(Object value) {
        if (value instanceof Material material) {
            return LegacyMaterials.name(material);
        }
        return value instanceof OldEnum<?> old ? old.name() : ((Enum<?>) value).name();
    }

    public static String toString(Object value) {
        if (value instanceof Material material) {
            return LegacyMaterials.name(material);
        }
        return value instanceof OldEnum<?> ? name(value) : value.toString();
    }

    public static int ordinal(Object value) {
        return value instanceof OldEnum<?> old ? old.ordinal() : ((Enum<?>) value).ordinal();
    }
}
