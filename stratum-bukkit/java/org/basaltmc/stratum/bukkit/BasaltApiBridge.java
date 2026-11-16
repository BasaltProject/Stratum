package org.basaltmc.stratum.bukkit;

import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import java.util.function.BooleanSupplier;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.basaltmc.stratum.bukkit.gen.InternalAPIBridgeBase;

/** The hooks Paper's API reaches the server through; what is not here reports itself once when used. */
public final class BasaltApiBridge extends InternalAPIBridgeBase {
    @Override
    public LifecycleEventManager<Plugin> createPluginLifecycleEventManager(JavaPlugin plugin,
            BooleanSupplier registrationCheck) {
        return new org.basaltmc.stratum.bukkit.brigadier.BasaltLifecycleManager(plugin);
    }

    @Override
    public <MODERN, LEGACY> org.bukkit.GameRule<LEGACY> legacyGameRuleBridge(org.bukkit.GameRule<MODERN> modern,
        java.util.function.Function<LEGACY, MODERN> toModern, java.util.function.Function<MODERN, LEGACY> fromModern,
        Class<LEGACY> type) {
        return new BasaltGameRule.Legacy<>(modern, toModern, fromModern, type);
    }

    @Override
    public io.papermc.paper.entity.poi.PoiType.Occupancy createOccupancy(String name) {
        return new io.papermc.paper.entity.poi.PoiType.Occupancy() {
            @Override
            public String toString() {
                return name;
            }
        };
    }

    @Override
    public org.bukkit.block.Biome constructLegacyCustomBiome() {
        return new BasaltBiome("minecraft:custom", -1);
    }

    @Override
    public org.bukkit.inventory.ItemStack createEmptyStack() {
        return new BasaltItemStack(org.bukkit.Material.AIR, 0);
    }

    @Override
    public org.bukkit.inventory.ItemStack deserializeItem(byte[] bytes) {
        return BasaltItemStack.restore(bytes);
    }

    @Override
    public org.bukkit.damage.DamageSource.Builder createDamageSourceBuilder(org.bukkit.damage.DamageType type) {
        return new BasaltDamageSource.Builder(type);
    }

    @Override
    public java.util.function.Predicate<io.papermc.paper.command.brigadier.CommandSourceStack> restricted(
            java.util.function.Predicate<io.papermc.paper.command.brigadier.CommandSourceStack> predicate,
            boolean restricted) {
        return predicate;
    }

    @Override
    public ComponentFlattener componentFlattener() {
        return ComponentFlattener.basic();
    }

    private static final java.util.Map<String, String> kEffectSounds = java.util.Map.of("hurt", "entity.player.hurt",
        "thorns", "enchant.thorns.hit", "drowning", "entity.player.hurt_drown", "burning", "entity.player.hurt_on_fire",
        "poking", "block.sweet_berry_bush.pick_berries", "freezing", "entity.player.hurt_freeze");

    static org.bukkit.damage.DamageEffect damageEffect(String name) {
        String sound = kEffectSounds.getOrDefault(name, kEffectSounds.get("hurt"));
        return () -> org.bukkit.Registry.SOUNDS.get(org.bukkit.NamespacedKey.minecraft(sound));
    }

    @Override
    public org.bukkit.damage.DamageEffect getDamageEffect(String id) {
        return damageEffect(id);
    }

    @Override
    public <C> io.papermc.paper.persistence.PersistentDataKey<C> createPersistentDataKey(net.kyori.adventure.key.Key key,
            org.bukkit.persistence.PersistentDataType<?, C> type) {
        org.bukkit.NamespacedKey own = new org.bukkit.NamespacedKey(key.namespace(), key.value());
        return new io.papermc.paper.persistence.PersistentDataKey<>() {
            @Override
            public org.bukkit.NamespacedKey key() {
                return own;
            }

            @Override
            public org.bukkit.persistence.PersistentDataType<?, C> dataType() {
                return type;
            }
        };
    }

    @Override
    public String getTranslationKey(org.bukkit.entity.EntityType type) {
        return "entity." + type.getKey().getNamespace() + "." + type.getKey().getKey();
    }

    @Override
    public org.bukkit.entity.SpawnCategory getSpawnCategory(org.bukkit.entity.EntityType type) {
        return switch (EntityCategories.of(type.getKey().getKey())) {
            case "monster" -> org.bukkit.entity.SpawnCategory.MONSTER;
            case "creature" -> org.bukkit.entity.SpawnCategory.ANIMAL;
            case "ambient" -> org.bukkit.entity.SpawnCategory.AMBIENT;
            case "axolotls" -> org.bukkit.entity.SpawnCategory.AXOLOTL;
            case "underground_water_creature" -> org.bukkit.entity.SpawnCategory.WATER_UNDERGROUND_CREATURE;
            case "water_creature" -> org.bukkit.entity.SpawnCategory.WATER_ANIMAL;
            case "water_ambient" -> org.bukkit.entity.SpawnCategory.WATER_AMBIENT;
            default -> org.bukkit.entity.SpawnCategory.MISC;
        };
    }

    @Override
    public boolean hasDefaultEntityAttributes(org.bukkit.NamespacedKey type) {
        return LeftoverNatives.entityDefaults(type.toString()).length > 0;
    }

    @Override
    public org.bukkit.attribute.Attributable getDefaultEntityAttributes(org.bukkit.NamespacedKey type) {
        String[] defaults = LeftoverNatives.entityDefaults(type.toString());
        if (defaults.length == 0) {
            throw new IllegalArgumentException("The entity type " + type + " has no attributes");
        }
        return new DefaultAttributes(defaults);
    }

    @Override
    public String getStatisticCriteriaKey(org.bukkit.Statistic statistic) {
        if (statistic.getType() == org.bukkit.Statistic.Type.UNTYPED) {
            return org.basaltmc.stratum.bukkit.player.PlayerCalls.statisticCriteria(statistic);
        }
        return "minecraft." + switch (statistic) {
            case MINE_BLOCK -> "mined";
            case USE_ITEM -> "used";
            case BREAK_ITEM -> "broken";
            case CRAFT_ITEM -> "crafted";
            case PICKUP -> "picked_up";
            case DROP -> "dropped";
            case KILL_ENTITY -> "killed";
            default -> "killed_by";
        };
    }

    @Override
    public io.papermc.paper.world.damagesource.CombatEntry createCombatEntry(org.bukkit.entity.LivingEntity entity,
            org.bukkit.damage.DamageSource source, float damage) {
        return new BasaltCombatEntry(source, damage, io.papermc.paper.world.damagesource.FallLocationType.GENERIC,
            entity.getFallDistance());
    }

    @Override
    public io.papermc.paper.world.damagesource.CombatEntry createCombatEntry(org.bukkit.damage.DamageSource source,
            float damage, io.papermc.paper.world.damagesource.FallLocationType location, float fallDistance) {
        return new BasaltCombatEntry(source, damage, location, fallDistance);
    }

    @Override
    public io.papermc.paper.datacomponent.item.ResolvableProfile defaultMannequinProfile() {
        return (io.papermc.paper.datacomponent.item.ResolvableProfile) org.basaltmc.stratum.bukkit.datacomponent.ContentSpecs
            .PROFILE.value(new java.util.LinkedHashMap<>());
    }

    @Override
    public net.kyori.adventure.text.Component defaultMannequinDescription() {
        return net.kyori.adventure.text.Component.translatable("entity.minecraft.mannequin.label");
    }

    @Override
    public java.util.Set<org.bukkit.entity.Pose> validMannequinPoses() {
        return java.util.EnumSet.of(org.bukkit.entity.Pose.STANDING, org.bukkit.entity.Pose.SNEAKING,
            org.bukkit.entity.Pose.SWIMMING, org.bukkit.entity.Pose.FALL_FLYING, org.bukkit.entity.Pose.SLEEPING);
    }

    @Override
    public com.destroystokyo.paper.SkinParts.Mutable allSkinParts() {
        return new org.basaltmc.stratum.bukkit.misc.BasaltSkinParts(0x7F);
    }

    @Override
    public net.kyori.adventure.text.Component resolveWithContext(net.kyori.adventure.text.Component component,
            org.bukkit.command.CommandSender context, org.bukkit.entity.Entity scoreboardSubject,
            boolean bypassPermissions) throws java.io.IOException {
        org.bukkit.command.CommandSender source = context != null && !bypassPermissions ? context
            : org.bukkit.Bukkit.getConsoleSender();
        return resolve(component, source, scoreboardSubject);
    }

    private static net.kyori.adventure.text.Component resolve(net.kyori.adventure.text.Component component,
            org.bukkit.command.CommandSender source, org.bukkit.entity.Entity subject) {
        net.kyori.adventure.text.Component out = component;
        if (component instanceof net.kyori.adventure.text.SelectorComponent selector) {
            java.util.List<net.kyori.adventure.text.Component> names = new java.util.ArrayList<>();
            for (org.bukkit.entity.Entity found : org.bukkit.Bukkit.selectEntities(source, selector.pattern())) {
                names.add(found.name());
            }
            net.kyori.adventure.text.Component separator = selector.separator() == null
                ? net.kyori.adventure.text.Component.text(", ", net.kyori.adventure.text.format.NamedTextColor.GRAY)
                : resolve(selector.separator(), source, subject);
            out = net.kyori.adventure.text.Component.join(
                net.kyori.adventure.text.JoinConfiguration.separator(separator), names).style(selector.style());
        } else if (component instanceof net.kyori.adventure.text.ScoreComponent score) {
            String holder = score.name();
            if (holder.equals("*") && subject != null) {
                holder = subject.getName();
            } else if (holder.startsWith("@")) {
                java.util.List<org.bukkit.entity.Entity> found = org.bukkit.Bukkit.selectEntities(source, holder);
                holder = found.isEmpty() ? holder : found.get(0).getName();
            }
            org.bukkit.scoreboard.Objective objective = org.bukkit.Bukkit.getScoreboardManager().getMainScoreboard()
                .getObjective(score.objective());
            out = objective == null || !objective.getScore(holder).isScoreSet()
                ? net.kyori.adventure.text.Component.empty().style(score.style())
                : net.kyori.adventure.text.Component.text(objective.getScore(holder).getScore()).style(score.style());
        } else if (component instanceof net.kyori.adventure.text.TranslatableComponent translatable
                && !translatable.arguments().isEmpty()) {
            java.util.List<net.kyori.adventure.text.Component> arguments = new java.util.ArrayList<>();
            for (net.kyori.adventure.text.TranslationArgument argument : translatable.arguments()) {
                arguments.add(argument.value() instanceof net.kyori.adventure.text.Component inner
                    ? resolve(inner, source, subject) : argument.asComponent());
            }
            out = translatable.arguments(arguments);
        }
        java.util.List<net.kyori.adventure.text.Component> children = new java.util.ArrayList<>();
        for (net.kyori.adventure.text.Component child : component.children()) {
            children.add(resolve(child, source, subject));
        }
        return children.isEmpty() ? out : out.children(children);
    }
}
