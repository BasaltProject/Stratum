package org.basaltmc.stratum.bukkit.legacy;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.loot.LootTables;
import org.bukkit.map.MapCursor;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

/** Targets of the static calls LegacyTransformer reroutes: the same lookups, with old constant names accepted. */
public final class LegacyNames {
    private static final ApiVersion kCurrent = new ApiVersion(Integer.MAX_VALUE, 0, 0);
    private static final ApiVersion kBeforeFieldNameParity = new ApiVersion(1, 20, 4);

    private LegacyNames() {}

    private static String of(ApiVersion version, String owner, String name) {
        return LegacyRenames.renameText(version, owner, name);
    }

    public static PatternType valueOf_PatternType(String name) {
        return PatternType.valueOf(of(kCurrent, "org/bukkit/block/banner/PatternType", name));
    }

    public static PatternType valueOf_PatternTypeOld(String name) {
        return PatternType.valueOf(of(kBeforeFieldNameParity, "org/bukkit/block/banner/PatternType", name));
    }

    public static MapCursor.Type valueOf_MapCursorType(String name) {
        return MapCursor.Type.valueOf(of(kCurrent, "org/bukkit/map/MapCursor$Type", name));
    }

    public static MapCursor.Type valueOf_MapCursorTypeOld(String name) {
        return MapCursor.Type.valueOf(of(kBeforeFieldNameParity, "org/bukkit/map/MapCursor$Type", name));
    }

    public static Enchantment getByName_Enchantment(String name) {
        return Enchantment.getByName(of(kCurrent, "org/bukkit/enchantments/Enchantment", name));
    }

    public static Biome valueOf_Biome(String name) {
        return Biome.valueOf(of(kCurrent, "org/bukkit/block/Biome", name));
    }

    public static EntityType valueOf_EntityType(String name) {
        return EntityType.valueOf(of(kCurrent, "org/bukkit/entity/EntityType", name));
    }

    public static EntityType fromName_EntityType(String name) {
        return EntityType.fromName(of(kCurrent, "org/bukkit/entity/EntityType", name));
    }

    public static PotionEffectType getByName_PotionEffectType(String name) {
        return PotionEffectType.getByName(of(kCurrent, "org/bukkit/potion/PotionEffectType", name));
    }

    public static PotionType valueOf_PotionType(String name) {
        return PotionType.valueOf(of(kCurrent, "org/bukkit/potion/PotionType", name));
    }

    public static Particle valueOf_Particle(String name) {
        return Particle.valueOf(of(kCurrent, "org/bukkit/Particle", name));
    }

    public static LootTables valueOf_LootTables(String name) {
        return LootTables.valueOf(of(kCurrent, "org/bukkit/loot/LootTables", name));
    }

    public static Attribute valueOf_Attribute(String name) {
        return Attribute.valueOf(of(kCurrent, "org/bukkit/attribute/Attribute", name));
    }

    public static ItemFlag valueOf_ItemFlag(String name) {
        return ItemFlag.valueOf(of(kCurrent, "org/bukkit/inventory/ItemFlag", name));
    }

    public static Sound valueOf_Sound(String name) {
        return Sound.valueOf(of(kCurrent, "org/bukkit/Sound", name));
    }

    private static final String[] kKeyedOwners = {"org/bukkit/enchantments/Enchantment",
        "org/bukkit/potion/PotionEffectType", "org/bukkit/attribute/Attribute", "org/bukkit/block/Biome",
        "org/bukkit/entity/EntityType"};

    /** A Registry lookup that retries the renamed key when a key from an older version finds nothing. */
    public static <T extends org.bukkit.Keyed> org.bukkit.Keyed registryGet(org.bukkit.Registry<T> registry,
            org.bukkit.NamespacedKey key) {
        T found = registry.get(key);
        if (found != null || key == null || !key.getNamespace().equals(org.bukkit.NamespacedKey.MINECRAFT)) {
            return found;
        }
        String upper = key.getKey().toUpperCase(java.util.Locale.ROOT).replace('.', '_');
        for (String owner : kKeyedOwners) {
            String renamed = LegacyRenames.rename(kCurrent, owner, upper);
            if (!renamed.equals(upper)) {
                found = registry.get(org.bukkit.NamespacedKey.minecraft(renamed.toLowerCase(java.util.Locale.ROOT)));
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    public static Enchantment getByKey_Enchantment(org.bukkit.NamespacedKey key) {
        return (Enchantment) registryGet(org.bukkit.Registry.ENCHANTMENT, key);
    }

    public static PotionEffectType getByKey_PotionEffectType(org.bukkit.NamespacedKey key) {
        return (PotionEffectType) registryGet(org.bukkit.Registry.EFFECT, key);
    }
}
