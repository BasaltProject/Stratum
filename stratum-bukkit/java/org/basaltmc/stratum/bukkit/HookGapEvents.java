package org.basaltmc.stratum.bukkit;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;

final class HookGapEvents {
    private HookGapEvents() {}

    static void install() {
        EventBridge.map("player.item_cooldown", io.papermc.paper.event.player.PlayerItemCooldownEvent.class, f -> {
            Material type = Material.matchMaterial(f.text("item"));
            NamespacedKey group = NamespacedKey.fromString(f.text("group"));
            return f.player() == null || type == null || group == null ? null
                : new io.papermc.paper.event.player.PlayerItemCooldownEvent(f.player(), type, group,
                    (int) f.whole("cooldown"));
        }, (e, f) -> f.put("cooldown", (long) ((io.papermc.paper.event.player.PlayerItemCooldownEvent) e).getCooldown()));
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
        EventBridge.map("entity.launch", com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent.class, f -> {
            org.bukkit.entity.Entity made = f.entity();
            org.bukkit.entity.Entity shooter = f.whole("shooter_id") < 0 ? null
                : BasaltServer.instance().entityById(f.world(), (int) f.whole("shooter_id"));
            if (!(made instanceof org.bukkit.entity.Projectile projectile) || !(shooter instanceof org.bukkit.entity.Player who)) {
                return null;
            }
            org.bukkit.inventory.ItemStack held = who.getInventory().getItemInMainHand();
            if (held == null || held.getType().isAir()) {
                held = who.getInventory().getItemInOffHand();
            }
            return new com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent(who, held.clone(), projectile);
        });
        EventBridge.map("block.shear_entity", org.bukkit.event.block.BlockShearEntityEvent.class, f -> {
            org.bukkit.inventory.ItemStack tool = PlayerEvents.stackOf(f.text("tool"), 1);
            return f.entity() == null || tool == null ? null
                : new org.bukkit.event.block.BlockShearEntityEvent(f.block(), f.entity(), tool,
                    new java.util.ArrayList<>());
        });
        EventBridge.map("player.shield_disable", io.papermc.paper.event.player.PlayerShieldDisableEvent.class, f -> {
            org.bukkit.entity.Entity attacker = BasaltServer.instance().entityById(f.world(), (int) f.whole("attacker_id"));
            return f.player() == null || attacker == null ? null
                : new io.papermc.paper.event.player.PlayerShieldDisableEvent(f.player(), attacker,
                    (int) f.whole("cooldown"));
        }, (e, f) -> f.put("cooldown", (long) ((io.papermc.paper.event.player.PlayerShieldDisableEvent) e).getCooldown()));
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
    }

    private static org.bukkit.potion.PotionEffectType effectOf(String id) {
        NamespacedKey key = id.isEmpty() ? null : NamespacedKey.fromString(id);
        return key == null ? null : org.bukkit.Registry.EFFECT.get(key);
    }
}
