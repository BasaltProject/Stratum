package org.basaltmc.stratum.bukkit.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.world.level.block.Block;

/** Default component maps of items, assembled from the native item tables. */
public final class ItemPrototypes {
    private static final Map<String, DataComponentMap> cache = new ConcurrentHashMap<>();
    private static final float kAxeDisablesBlockingSeconds = 5.0F;

    private ItemPrototypes() {}

    public static void invalidate() {
        cache.clear();
    }

    public static DataComponentMap of(Item item) {
        if (item == null) {
            return DataComponentMap.EMPTY;
        }
        return cache.computeIfAbsent(item.itemKey().toString(), id -> build(id, ItemData.of(id)));
    }

    private static DataComponentMap build(String id, ItemData data) {
        DataComponentMap.Builder builder = DataComponentMap.builder();
        int maxDamage = data.maxDamage();
        builder.set(DataComponents.MAX_STACK_SIZE, maxDamage > 0 ? 1 : data.maxStackSize());
        if (maxDamage > 0) {
            builder.set(DataComponents.MAX_DAMAGE, maxDamage);
            builder.set(DataComponents.DAMAGE, 0);
        }
        Tool tool = tool(data.tool());
        if (tool != null) {
            builder.set(DataComponents.TOOL, tool);
            if (data.weapon() != null) {
                builder.set(DataComponents.WEAPON, weapon(id, tool));
            }
        }
        Map<String, Object> food = data.food();
        if (food != null) {
            builder.set(DataComponents.FOOD, new FoodProperties(Nbt.integer(food, "nutrition", 0),
                (float) Nbt.real(food, "saturation", 0.0), Nbt.flag(food, "can_always_eat", false)));
        }
        return builder.build();
    }

    private static Tool tool(Map<String, Object> data) {
        if (data == null) {
            return null;
        }
        List<Tool.Rule> rules = new ArrayList<>();
        List<Object> entries = Nbt.list(data, "rules");
        for (Object entry : entries == null ? List.of() : entries) {
            Map<String, Object> rule = Nbt.map(entry);
            String blocks = Nbt.string(rule, "blocks", "");
            HolderSet<Block> set;
            if (blocks.startsWith("#")) {
                set = new HolderSet.Named<>(TagKey.create(Registries.BLOCK, Identifier.parse(blocks.substring(1))));
            } else if (!blocks.isEmpty()) {
                set = HolderSet.direct(BuiltInRegistries.BLOCK.wrapAsHolder(BuiltInRegistries.BLOCK.getValue(Identifier.parse(blocks))));
            } else {
                continue;
            }
            boolean correct = Nbt.flag(rule, "correct_for_drops", false);
            Optional<Float> speed = rule.containsKey("speed") ? Optional.of((float) Nbt.real(rule, "speed", 1.0)) : Optional.empty();
            // A rule that sets a speed without granting drops only overrides the speed.
            Optional<Boolean> drops = correct ? Optional.of(true) : speed.isPresent() ? Optional.empty() : Optional.of(false);
            rules.add(new Tool.Rule(set, speed, drops));
        }
        return new Tool(rules, 1.0F, Nbt.integer(data, "damage_per_block", 1), Nbt.flag(data, "can_destroy_blocks_in_creative", true));
    }

    private static Weapon weapon(String id, Tool tool) {
        if (id.endsWith("_axe")) {
            return new Weapon(2, kAxeDisablesBlockingSeconds);
        }
        return tool.damagePerBlock() == 2 ? new Weapon(1) : new Weapon(2, 0.0F);
    }
}
