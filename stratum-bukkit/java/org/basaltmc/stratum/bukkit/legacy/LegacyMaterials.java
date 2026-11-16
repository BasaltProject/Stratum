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
        static final Map<Material, Integer> kIndex = new HashMap<>();
        static final Map<Material, Material> kModern = new java.util.concurrent.ConcurrentHashMap<>();
        static final Map<Material, Material> kLegacyOf = new java.util.concurrent.ConcurrentHashMap<>();

        static {
            for (int at = 0; at < kLegacy.length; at++) {
                kById.putIfAbsent(kLegacy[at].getId(), kLegacy[at]);
                kIndex.put(kLegacy[at], at);
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
            Material legacy = Material.getMaterial(Material.LEGACY_PREFIX + name);
            return legacy != null ? legacy : Material.valueOf(name);
        }
        return Material.valueOf(name);
    }

    public static Material getMaterial(String name) {
        if (name == null || name.startsWith(Material.LEGACY_PREFIX)) {
            return Material.getMaterial(name);
        }
        Material legacy = Material.getMaterial(Material.LEGACY_PREFIX + name);
        return legacy != null ? legacy : Material.getMaterial(name);
    }

    public static Material matchMaterial(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        String plain = name.trim().toUpperCase(java.util.Locale.ROOT).replace(' ', '_').replace("MINECRAFT:", "");
        Material found = getMaterial(plain);
        return found != null ? found : toLegacy(Material.matchMaterial(name));
    }

    /** The index among the LEGACY_ constants, which is what values() lists; a modern one has none and gets the last. */
    public static int ordinal(Material material) {
        Integer at = material.isLegacy() ? Holder.kIndex.get(material) : null;
        return at != null ? at : Holder.kLegacy.length - 1;
    }

    public static boolean isTagged(org.bukkit.Tag<?> tag, org.bukkit.Keyed value) {
        @SuppressWarnings("unchecked")
        org.bukkit.Tag<org.bukkit.Keyed> typed = (org.bukkit.Tag<org.bukkit.Keyed>) tag;
        return typed.isTagged(value instanceof Material material ? modern(material) : value);
    }

    /** The modern Material a call into the API takes for the LEGACY_ constant a plugin passes. */
    public static Material modern(Material material) {
        if (material == null || !material.isLegacy()) {
            return material;
        }
        Material found = Holder.kModern.get(material);
        if (found == null) {
            found = org.bukkit.Bukkit.getUnsafe().fromLegacy(material);
            Holder.kModern.put(material, found);
        }
        return found;
    }

    public static Material modern(Material material, byte data) {
        return material != null && material.isLegacy() && data != 0 && material.getId() < 256
            ? org.bukkit.Bukkit.getUnsafe().fromLegacy(material, data).getMaterial() : modern(material);
    }

    /** The LEGACY_ constant a pre-flattening plugin compares a Material from the API against. */
    public static Material legacy(Material material) {
        if (material == null || material.isLegacy()) {
            return material;
        }
        Material found = Holder.kLegacyOf.get(material);
        if (found == null) {
            found = toLegacy(material);
            Holder.kLegacyOf.put(material, found);
        }
        return found;
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
        LegacyItems.Entry item = LegacyItems.entryOf(material);
        if (item != null) {
            return item.legacy();
        }
        Material byName = Material.getMaterial(Material.LEGACY_PREFIX + material.name());
        return byName != null ? byName : material;
    }

    /** The data byte a modern material carries as a legacy item, 0 when it is the plain form. */
    public static byte legacyData(Material material) {
        if (material.isLegacy()) {
            return 0;
        }
        if (material.isBlock()) {
            return LegacyBlocks.dataOf(material.createBlockData());
        }
        LegacyItems.Entry item = LegacyItems.entryOf(material);
        return item == null ? 0 : item.data();
    }

    public static boolean isVariant(Material legacy) {
        return legacy.isLegacy() && LegacyItems.isVariant(legacy);
    }

    public static Material withData(Material legacy, short data) {
        return LegacyItems.withData(legacy, data);
    }

    private static boolean holdsMaterials(java.util.Collection<?> values) {
        for (Object each : values) {
            if (each != null) {
                return each instanceof Material;
            }
        }
        return false;
    }

    private static <T extends java.util.Collection<Object>> T convert(java.util.Collection<?> from, T into,
            java.util.function.UnaryOperator<Material> step) {
        for (Object each : from) {
            into.add(each instanceof Material material ? step.apply(material) : each);
        }
        return into;
    }

    public static java.util.Set<?> legacySet(java.util.Set<?> values) {
        return values == null || !holdsMaterials(values) ? values
            : convert(values, new java.util.LinkedHashSet<>(), LegacyMaterials::legacy);
    }

    public static java.util.Set<?> modernSet(java.util.Set<?> values) {
        return values == null || !holdsMaterials(values) ? values
            : convert(values, new java.util.LinkedHashSet<>(), LegacyMaterials::modern);
    }

    public static java.util.List<?> legacyList(java.util.List<?> values) {
        return values == null || !holdsMaterials(values) ? values
            : convert(values, new java.util.ArrayList<>(), LegacyMaterials::legacy);
    }

    public static java.util.List<?> modernList(java.util.List<?> values) {
        return values == null || !holdsMaterials(values) ? values
            : convert(values, new java.util.ArrayList<>(), LegacyMaterials::modern);
    }

    public static java.util.Collection<?> legacyCollection(java.util.Collection<?> values) {
        return values == null || !holdsMaterials(values) ? values
            : convert(values, new java.util.ArrayList<>(), LegacyMaterials::legacy);
    }

    public static java.util.Collection<?> modernCollection(java.util.Collection<?> values) {
        return values == null || !holdsMaterials(values) ? values
            : convert(values, new java.util.ArrayList<>(), LegacyMaterials::modern);
    }

    public static Material[] legacyArray(Material[] values) {
        return mapArray(values, LegacyMaterials::legacy);
    }

    public static Material[] modernArray(Material[] values) {
        return mapArray(values, LegacyMaterials::modern);
    }

    private static Material[] mapArray(Material[] values, java.util.function.UnaryOperator<Material> step) {
        if (values == null) {
            return null;
        }
        Material[] out = values.clone();
        for (int at = 0; at < out.length; at++) {
            out[at] = step.apply(out[at]);
        }
        return out;
    }

    public static BlockData blockData(Material legacy, byte data) {
        return org.bukkit.Bukkit.getUnsafe().fromLegacy(legacy, data);
    }
}
