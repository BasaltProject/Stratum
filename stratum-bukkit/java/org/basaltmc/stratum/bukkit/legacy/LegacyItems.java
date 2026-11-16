package org.basaltmc.stratum.bukkit.legacy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

/** The numeric id and data byte a pre-flattening plugin sees for an item, the inverse of the item flattening. */
final class LegacyItems {
    record Entry(Material legacy, byte data) {}

    private static final int kFirstItemOnlyId = 256;
    private static final int kSpawnEggId = 383;
    private static final Object kLock = new Object();
    private static final ConcurrentHashMap<Material, Boolean> kVariant = new ConcurrentHashMap<>();
    private static volatile Map<Material, Entry> kByModern;

    private LegacyItems() {}

    private static int dataLimit(Material legacy) {
        return legacy.getId() == kSpawnEggId ? 128 : 16;
    }

    private static Material modernOf(Material legacy, int data) {
        try {
            Material modern = Bukkit.getUnsafe().fromLegacy(new MaterialData(legacy, (byte) data));
            return modern == null || modern.isAir() ? null : modern;
        } catch (RuntimeException e) {
            return null;
        }
    }

    private static Map<Material, Entry> table() {
        Map<Material, Entry> built = kByModern;
        if (built == null) {
            synchronized (kLock) {
                built = kByModern;
                if (built == null) {
                    built = new HashMap<>();
                    for (Material legacy : LegacyMaterials.values()) {
                        if (legacy.getId() < kFirstItemOnlyId) {
                            continue;
                        }
                        for (int data = 0; data < dataLimit(legacy); data++) {
                            Material modern = modernOf(legacy, data);
                            if (modern != null) {
                                built.putIfAbsent(modern, new Entry(legacy, (byte) data));
                            }
                        }
                    }
                    kByModern = built;
                }
            }
        }
        return built;
    }

    /** The legacy item and data of a modern item that only exists as a variant or rename, or null for the plain ones. */
    static Entry entryOf(Material modern) {
        if (modern.isBlock()) {
            return null;
        }
        Material same = Material.getMaterial(Material.LEGACY_PREFIX + modern.name());
        if (same != null && same.getId() >= kFirstItemOnlyId) {
            return new Entry(same, (byte) 0);
        }
        return table().get(modern);
    }

    /** Whether the data byte of this legacy material chooses a different modern material. */
    static boolean isVariant(Material legacy) {
        return kVariant.computeIfAbsent(legacy, material -> {
            Set<Material> seen = new HashSet<>();
            for (int data = 0; data < dataLimit(material); data++) {
                Material modern = modernOf(material, data);
                if (modern != null) {
                    seen.add(modern);
                }
            }
            return seen.size() > 1;
        });
    }

    static Material withData(Material legacy, int data) {
        return modernOf(legacy, data);
    }
}
