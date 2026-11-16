package org.basaltmc.stratum.bukkit.legacy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

/** The Material statics a pre-flattening plugin calls, answered with the LEGACY_ constants the way it expects. */
public final class LegacyMaterials {
    private static final class Holder {
        static final Material[] kLegacy = Arrays.stream(Material.values()).filter(Material::isLegacy)
            .toArray(Material[]::new);
        static final Map<Integer, Material> kById = new HashMap<>();

        static {
            for (Material each : kLegacy) {
                kById.putIfAbsent(each.getId(), each);
            }
        }
    }

    private LegacyMaterials() {}

    public static Material[] values() {
        return Holder.kLegacy.clone();
    }

    public static Material getMaterial(int id) {
        return Holder.kById.get(id);
    }

    public static Material valueOf(String name) {
        Objects.requireNonNull(name, "Name is null");
        if (!name.startsWith(Material.LEGACY_PREFIX)) {
            Material modern = Material.getMaterial(name);
            if (modern != null) {
                return modern;
            }
            return Material.valueOf(Material.LEGACY_PREFIX + name);
        }
        return Material.valueOf(name);
    }

    public static Material getMaterial(String name) {
        Material found = Material.getMaterial(name);
        return found != null || name == null || name.startsWith(Material.LEGACY_PREFIX) ? found
            : Material.getMaterial(Material.LEGACY_PREFIX + name);
    }

    public static Material matchMaterial(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        String plain = name.trim().toUpperCase(java.util.Locale.ROOT).replace(' ', '_').replace("MINECRAFT:", "");
        Material found = getMaterial(plain);
        return found != null ? found : Material.matchMaterial(name);
    }

    public static String name(Material material) {
        return material.isLegacy() ? material.name().substring(Material.LEGACY_PREFIX.length()) : material.name();
    }

    public static String toString(Material material) {
        return name(material);
    }

    /** The legacy material for a modern one, as Bukkit's toLegacy: the numeric-id twin, or the same one when none. */
    public static Material toLegacy(Material material) {
        if (material.isLegacy()) {
            return material;
        }
        if (material.isBlock()) {
            int block = LegacyBlocks.blockOf(material.createBlockData());
            Material found = block == 0 && material != Material.AIR ? null : getMaterial(block);
            if (found != null) {
                return found;
            }
        }
        Material byName = Material.getMaterial(Material.LEGACY_PREFIX + material.name());
        return byName != null ? byName : material;
    }

    /** The data byte a modern material carries as a legacy item, 0 when it is the plain form. */
    public static byte legacyData(Material material) {
        return material.isBlock() && !material.isLegacy() ? LegacyBlocks.dataOf(material.createBlockData()) : 0;
    }

    public static BlockData blockData(Material legacy, byte data) {
        return org.bukkit.Bukkit.getUnsafe().fromLegacy(legacy, data);
    }
}
