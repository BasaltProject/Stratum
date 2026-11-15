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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.item.enchantment.Repairable;
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
        builder.set(DataComponents.RARITY, rarity(data.rarity()));
        builder.set(DataComponents.ATTRIBUTE_MODIFIERS, attributes(data.weapon(), data.armor()));
        if (data.enchantable() > 0) {
            builder.set(DataComponents.ENCHANTABLE, new Enchantable(data.enchantable()));
        }
        Repairable repairable = repairable(data.repairable());
        if (repairable != null) {
            builder.set(DataComponents.REPAIRABLE, repairable);
        }
        if (food != null) {
            builder.set(DataComponents.CONSUMABLE, consumable(id, food));
        }
        return builder.build();
    }

    private static Rarity rarity(String name) {
        for (Rarity rarity : Rarity.values()) {
            if (rarity.name().equalsIgnoreCase(name)) {
                return rarity;
            }
        }
        return Rarity.COMMON;
    }

    private static Repairable repairable(String target) {
        if (target == null) {
            return null;
        }
        if (target.startsWith("#")) {
            return new Repairable(new HolderSet.Named<>(TagKey.create(Registries.ITEM, Identifier.parse(target.substring(1)))));
        }
        Item item = BuiltInRegistries.ITEM.getValue(Identifier.parse(target));
        return new Repairable(HolderSet.direct(BuiltInRegistries.ITEM.wrapAsHolder(item)));
    }

    private static Consumable consumable(String id, Map<String, Object> food) {
        boolean drink = id.endsWith("_bottle") || id.equals("minecraft:potion") || id.equals("minecraft:milk_bucket");
        return Consumable.builder()
            .consumeSeconds((float) Nbt.real(food, "consume_seconds", Consumable.DEFAULT_CONSUME_SECONDS))
            .animation(drink ? ItemUseAnimation.DRINK : ItemUseAnimation.EAT)
            .sound(drink ? SoundEvents.GENERIC_DRINK : SoundEvents.GENERIC_EAT)
            .build();
    }

    private static ItemAttributeModifiers attributes(Map<String, Object> weapon, Map<String, Object> armor) {
        ItemAttributeModifiers.Builder modifiers = ItemAttributeModifiers.builder();
        if (weapon != null) {
            modifiers.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID,
                Nbt.real(weapon, "attack_damage", 0.0), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
            modifiers.add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID,
                Nbt.real(weapon, "attack_speed", 0.0), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        }
        if (armor != null) {
            String slot = Nbt.string(armor, "slot", "");
            EquipmentSlotGroup group = switch (slot) {
                case "head" -> EquipmentSlotGroup.HEAD;
                case "chest" -> EquipmentSlotGroup.CHEST;
                case "legs" -> EquipmentSlotGroup.LEGS;
                case "feet" -> EquipmentSlotGroup.FEET;
                case "body" -> EquipmentSlotGroup.BODY;
                default -> null;
            };
            if (group != null) {
                String type = switch (slot) {
                    case "head" -> "helmet";
                    case "chest" -> "chestplate";
                    case "legs" -> "leggings";
                    case "feet" -> "boots";
                    default -> "body";
                };
                Identifier modifierId = Identifier.withDefaultNamespace("armor." + type);
                modifiers.add(Attributes.ARMOR, new AttributeModifier(modifierId, Nbt.real(armor, "armor", 0.0), AttributeModifier.Operation.ADD_VALUE), group);
                modifiers.add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(modifierId, Nbt.real(armor, "toughness", 0.0), AttributeModifier.Operation.ADD_VALUE), group);
                double knockback = Nbt.real(armor, "knockback_resistance", 0.0);
                if (knockback > 0.0) {
                    modifiers.add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(modifierId, knockback, AttributeModifier.Operation.ADD_VALUE), group);
                }
            }
        }
        return modifiers.build();
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
