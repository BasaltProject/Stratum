package org.basaltmc.stratum.bukkit;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

final class HookGapEvents {
    private HookGapEvents() {}

    static void install() {
        EventBridge.map("player.stop_using_item", io.papermc.paper.event.player.PlayerStopUsingItemEvent.class, f -> {
            org.bukkit.inventory.ItemStack stack = f.stack("item");
            return f.player() == null || stack == null ? null
                : new io.papermc.paper.event.player.PlayerStopUsingItemEvent(f.player(), stack, (int) f.whole("ticks"));
        });
        EventBridge.map("player.name_entity", io.papermc.paper.event.player.PlayerNameEntityEvent.class, f ->
            f.player() != null && f.entity() instanceof org.bukkit.entity.LivingEntity living
                ? new io.papermc.paper.event.player.PlayerNameEntityEvent(f.player(), living,
                    net.kyori.adventure.text.Component.text(f.text("name")), f.flag("persistent")) : null,
            (e, f) -> {
                io.papermc.paper.event.player.PlayerNameEntityEvent named = (io.papermc.paper.event.player.PlayerNameEntityEvent) e;
                f.put("name", net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                    .serialize(named.getName()));
                f.put("persistent", named.isPersistent());
            });
        EventBridge.map("player.exhaustion", org.bukkit.event.entity.EntityExhaustionEvent.class, f -> {
            org.bukkit.event.entity.EntityExhaustionEvent.ExhaustionReason reason;
            try {
                reason = org.bukkit.event.entity.EntityExhaustionEvent.ExhaustionReason.valueOf(
                    f.text("reason").toUpperCase(java.util.Locale.ROOT));
            } catch (IllegalArgumentException unknown) {
                reason = org.bukkit.event.entity.EntityExhaustionEvent.ExhaustionReason.UNKNOWN;
            }
            return f.player() == null ? null
                : new org.bukkit.event.entity.EntityExhaustionEvent(f.player(), reason, (float) f.real("exhaustion"));
        }, (e, f) -> f.put("exhaustion", (double) ((org.bukkit.event.entity.EntityExhaustionEvent) e).getExhaustion()));
        EventBridge.map("block.dispense_armor", org.bukkit.event.block.BlockDispenseArmorEvent.class, f -> {
            org.bukkit.inventory.ItemStack stack = PlayerEvents.stackOf(f.text("item"), 1);
            return !(f.entity() instanceof org.bukkit.entity.LivingEntity living) || stack == null ? null
                : new org.bukkit.event.block.BlockDispenseArmorEvent(f.block(), stack, living);
        });
        EventBridge.map("block.shear_entity", org.bukkit.event.block.BlockShearEntityEvent.class, f -> {
            org.bukkit.inventory.ItemStack tool = PlayerEvents.stackOf(f.text("tool"), 1);
            return f.entity() == null || tool == null ? null
                : new org.bukkit.event.block.BlockShearEntityEvent(f.block(), f.entity(), tool,
                    new java.util.ArrayList<>());
        });
        EventBridge.map("player.item_frame_change", io.papermc.paper.event.player.PlayerItemFrameChangeEvent.class, f -> {
            org.bukkit.inventory.ItemStack stack = PlayerEvents.stackOf(f.text("item"), 1);
            if (f.player() == null || !(f.entity() instanceof org.bukkit.entity.ItemFrame frame) || stack == null) {
                return null;
            }
            io.papermc.paper.event.player.PlayerItemFrameChangeEvent.ItemFrameChangeAction action = switch (f.text("action")) {
                case "remove" -> io.papermc.paper.event.player.PlayerItemFrameChangeEvent.ItemFrameChangeAction.REMOVE;
                case "rotate" -> io.papermc.paper.event.player.PlayerItemFrameChangeEvent.ItemFrameChangeAction.ROTATE;
                default -> io.papermc.paper.event.player.PlayerItemFrameChangeEvent.ItemFrameChangeAction.PLACE;
            };
            return new io.papermc.paper.event.player.PlayerItemFrameChangeEvent(f.player(), frame, stack, action);
        });
        EventBridge.map("player.change_beacon_effect", io.papermc.paper.event.player.PlayerChangeBeaconEffectEvent.class, f ->
            f.player() == null ? null : new io.papermc.paper.event.player.PlayerChangeBeaconEffectEvent(f.player(),
                effectOf(f.text("primary")), effectOf(f.text("secondary")), f.block()),
            (e, f) -> {
                io.papermc.paper.event.player.PlayerChangeBeaconEffectEvent beacon =
                    (io.papermc.paper.event.player.PlayerChangeBeaconEffectEvent) e;
                f.put("primary", beacon.getPrimary() == null ? "" : beacon.getPrimary().getKey().toString());
                f.put("secondary", beacon.getSecondary() == null ? "" : beacon.getSecondary().getKey().toString());
            });
        EventBridge.map("player.elytra_boost", com.destroystokyo.paper.event.player.PlayerElytraBoostEvent.class, f -> {
            if (f.player() == null || !(org.basaltmc.stratum.bukkit.entity.EntityCore.template(f.world(),
                    org.bukkit.entity.EntityType.FIREWORK_ROCKET, f.player().getLocation().getX(),
                    f.player().getLocation().getY(), f.player().getLocation().getZ(), 0f, 0f)
                        instanceof org.bukkit.entity.Firework rocket)) {
                return null;
            }
            org.bukkit.inventory.EquipmentSlot hand = PlayerEvents.hand(f.text("hand"));
            org.bukkit.inventory.ItemStack used = hand == org.bukkit.inventory.EquipmentSlot.OFF_HAND
                ? f.player().getInventory().getItemInOffHand() : f.player().getInventory().getItemInMainHand();
            return new com.destroystokyo.paper.event.player.PlayerElytraBoostEvent(f.player(), used.clone(), rocket, hand);
        }, (e, f) -> f.put("consume", ((com.destroystokyo.paper.event.player.PlayerElytraBoostEvent) e).shouldConsume()));
        EventBridge.map("player.bed_try", org.bukkit.event.player.PlayerBedEnterEvent.class, f -> {
            String reason = f.text("result");
            org.bukkit.event.player.PlayerBedEnterEvent.BedEnterResult result = switch (reason) {
                case "not_possible_here" -> org.bukkit.event.player.PlayerBedEnterEvent.BedEnterResult.NOT_POSSIBLE_HERE;
                case "not_possible_now" -> org.bukkit.event.player.PlayerBedEnterEvent.BedEnterResult.NOT_POSSIBLE_NOW;
                case "too_far_away" -> org.bukkit.event.player.PlayerBedEnterEvent.BedEnterResult.TOO_FAR_AWAY;
                case "obstructed" -> org.bukkit.event.player.PlayerBedEnterEvent.BedEnterResult.OBSTRUCTED;
                case "not_safe" -> org.bukkit.event.player.PlayerBedEnterEvent.BedEnterResult.NOT_SAFE;
                default -> org.bukkit.event.player.PlayerBedEnterEvent.BedEnterResult.OK;
            };
            return f.player() == null ? null : new org.bukkit.event.player.PlayerBedEnterEvent(f.player(), f.block(),
                result, new PlayerEvents.BedAction(reason, f.text("can_sleep"), null));
        }, (e, f) -> f.put("use_bed", ((org.bukkit.event.player.PlayerBedEnterEvent) e).useBed().name().toLowerCase()));
        EventBridge.map("player.bed_fail_enter", io.papermc.paper.event.player.PlayerBedFailEnterEvent.class, f -> {
            String reason = f.text("reason");
            io.papermc.paper.event.player.PlayerBedFailEnterEvent.FailReason fail = switch (reason) {
                case "not_possible_here" -> io.papermc.paper.event.player.PlayerBedFailEnterEvent.FailReason.NOT_POSSIBLE_HERE;
                case "too_far_away" -> io.papermc.paper.event.player.PlayerBedFailEnterEvent.FailReason.TOO_FAR_AWAY;
                case "obstructed" -> io.papermc.paper.event.player.PlayerBedFailEnterEvent.FailReason.OBSTRUCTED;
                case "not_safe" -> io.papermc.paper.event.player.PlayerBedFailEnterEvent.FailReason.NOT_SAFE;
                case "other_problem" -> io.papermc.paper.event.player.PlayerBedFailEnterEvent.FailReason.OTHER_PROBLEM;
                default -> io.papermc.paper.event.player.PlayerBedFailEnterEvent.FailReason.NOT_POSSIBLE_NOW;
            };
            String key = f.text("message");
            Component message = key.isEmpty() ? null : Component.translatable(key);
            return f.player() == null ? null : new io.papermc.paper.event.player.PlayerBedFailEnterEvent(f.player(),
                fail, f.block(), f.flag("will_explode"), message, new PlayerEvents.BedAction(reason, "denied", message));
        }, (e, f) -> {
            io.papermc.paper.event.player.PlayerBedFailEnterEvent bed = (io.papermc.paper.event.player.PlayerBedFailEnterEvent) e;
            f.put("will_explode", bed.getWillExplode());
            Component message = bed.getMessage();
            f.put("message", message == null ? ""
                : message instanceof net.kyori.adventure.text.TranslatableComponent t ? t.key()
                : net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(message));
        });
        EventBridge.map("player.stonecutter_select", io.papermc.paper.event.player.PlayerStonecutterRecipeSelectEvent.class,
            f -> {
                org.basaltmc.stratum.bukkit.inv.BasaltInventoryView window = f.player() == null ? null
                    : org.basaltmc.stratum.bukkit.inv.Windows.current(f.player());
                org.bukkit.inventory.Recipe recipe = InventoryEvents.recipeOf(f.text("recipe"));
                if (window == null || !(recipe instanceof org.bukkit.inventory.StonecuttingRecipe cutting)) {
                    return null;
                }
                org.bukkit.inventory.Inventory top = org.basaltmc.stratum.bukkit.inv.TypedInventories.wrap(
                    org.basaltmc.stratum.bukkit.inv.TypedInventories.unwrap(window.getTopInventory()), null,
                    java.util.Map.of());
                return top instanceof org.bukkit.inventory.StonecutterInventory cutter
                    ? new io.papermc.paper.event.player.PlayerStonecutterRecipeSelectEvent(f.player(), cutter, cutting)
                    : null;
            }, (e, f) -> f.put("recipe", ((io.papermc.paper.event.player.PlayerStonecutterRecipeSelectEvent) e)
                .getStonecuttingRecipe().getKey().toString()));
        EventBridge.map("player.loom_select", io.papermc.paper.event.player.PlayerLoomPatternSelectEvent.class, f -> {
            org.basaltmc.stratum.bukkit.inv.BasaltInventoryView window = f.player() == null ? null
                : org.basaltmc.stratum.bukkit.inv.Windows.current(f.player());
            NamespacedKey key = NamespacedKey.fromString(f.text("pattern"));
            org.bukkit.block.banner.PatternType type = key == null ? null : org.bukkit.Registry.BANNER_PATTERN.get(key);
            if (window == null || type == null) {
                return null;
            }
            org.bukkit.inventory.Inventory top = org.basaltmc.stratum.bukkit.inv.TypedInventories.wrap(
                org.basaltmc.stratum.bukkit.inv.TypedInventories.unwrap(window.getTopInventory()), null,
                java.util.Map.of());
            return top instanceof org.bukkit.inventory.LoomInventory loom
                ? new io.papermc.paper.event.player.PlayerLoomPatternSelectEvent(f.player(), loom, type) : null;
        }, (e, f) -> f.put("pattern", ((io.papermc.paper.event.player.PlayerLoomPatternSelectEvent) e)
            .getPatternType().getKey().toString()));
        EventBridge.map("block.target_hit", io.papermc.paper.event.block.TargetHitEvent.class, f -> {
            org.bukkit.block.BlockFace face = switch (f.text("face")) {
                case "down" -> org.bukkit.block.BlockFace.DOWN;
                case "north" -> org.bukkit.block.BlockFace.NORTH;
                case "south" -> org.bukkit.block.BlockFace.SOUTH;
                case "west" -> org.bukkit.block.BlockFace.WEST;
                case "east" -> org.bukkit.block.BlockFace.EAST;
                default -> org.bukkit.block.BlockFace.UP;
            };
            return BasaltServer.instance().entityById(f.world(), (int) f.whole("projectile_id"))
                instanceof org.bukkit.entity.Projectile projectile
                ? new io.papermc.paper.event.block.TargetHitEvent(projectile, f.block(), face, (int) f.whole("signal"))
                : null;
        }, (e, f) -> f.put("signal", (long) ((io.papermc.paper.event.block.TargetHitEvent) e).getSignalStrength()));
        EventBridge.map("entity.jump", com.destroystokyo.paper.event.entity.EntityJumpEvent.class,
            f -> f.entity() instanceof org.bukkit.entity.LivingEntity living
                ? new com.destroystokyo.paper.event.entity.EntityJumpEvent(living) : null);
        EventBridge.map("entity.horse_jump", org.bukkit.event.entity.HorseJumpEvent.class,
            f -> f.entity() instanceof org.bukkit.entity.AbstractHorse horse
                ? new org.bukkit.event.entity.HorseJumpEvent(horse, (float) f.real("power")) : null);
        EventBridge.map("entity.portal_ready", io.papermc.paper.event.entity.EntityPortalReadyEvent.class, f -> {
            org.bukkit.World target = org.bukkit.Bukkit.getWorld(f.text("to_world"));
            return f.entity() == null || target == null ? null
                : new io.papermc.paper.event.entity.EntityPortalReadyEvent(f.entity(), target, org.bukkit.PortalType.NETHER);
        });
        EventBridge.map("entity.portal_exit", org.bukkit.event.entity.EntityPortalExitEvent.class, f -> {
            org.bukkit.World target = org.bukkit.Bukkit.getWorld(f.text("to_world"));
            if (f.entity() == null || target == null) {
                return null;
            }
            org.bukkit.util.Vector velocity = f.entity().getVelocity();
            return new org.bukkit.event.entity.EntityPortalExitEvent(f.entity(), f.entity().getLocation(),
                new Location(target, f.real("to_x"), f.real("to_y"), f.real("to_z")), velocity, velocity.clone());
        }, (e, f) -> {
            Location to = ((org.bukkit.event.entity.EntityPortalExitEvent) e).getTo();
            f.put("to_x", to.getX());
            f.put("to_y", to.getY());
            f.put("to_z", to.getZ());
        });
    }


    private static org.bukkit.potion.PotionEffectType effectOf(String id) {
        NamespacedKey key = id.isEmpty() ? null : NamespacedKey.fromString(id);
        return key == null ? null : org.bukkit.Registry.EFFECT.get(key);
    }
}