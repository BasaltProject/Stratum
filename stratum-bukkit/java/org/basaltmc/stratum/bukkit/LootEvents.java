package org.basaltmc.stratum.bukkit;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;

final class LootEvents {
    private LootEvents() {}

    static String lines(List<ItemStack> stacks) {
        StringBuilder lines = new StringBuilder();
        for (ItemStack stack : stacks) {
            if (stack != null && !stack.getType().isAir() && stack.getAmount() > 0) {
                lines.append(lines.length() == 0 ? "" : "\n").append(stack.getType().getKey()).append('|')
                    .append(stack.getAmount());
            }
        }
        return lines.toString();
    }

    static List<ItemStack> stacks(String lines) {
        List<ItemStack> loot = new ArrayList<>();
        for (String line : lines.split("\n")) {
            int bar = line.indexOf('|');
            ItemStack stack = bar < 0 ? null : PlayerEvents.stackOf(line.substring(0, bar),
                Long.parseLong(line.substring(bar + 1)));
            if (stack != null) {
                loot.add(stack);
            }
        }
        return loot;
    }

    static void install() {
        EventBridge.map("block.loot_generate", org.bukkit.event.world.LootGenerateEvent.class, f -> {
            BasaltWorld world = f.world();
            NamespacedKey key = NamespacedKey.fromString(f.text("table"));
            org.bukkit.loot.LootTable table = key == null ? null : Bukkit.getLootTable(key);
            if (world == null || table == null) {
                return null;
            }
            Player who = f.player();
            Block block = f.block();
            LootContext.Builder builder = new LootContext.Builder(new Location(world, block.getX() + 0.5,
                block.getY() + 0.5, block.getZ() + 0.5));
            if (who != null) {
                builder.lootedEntity(who).luck(who.getAttribute(org.bukkit.attribute.Attribute.LUCK) == null ? 0f
                    : (float) who.getAttribute(org.bukkit.attribute.Attribute.LUCK).getValue());
            }
            org.bukkit.inventory.InventoryHolder holder = block.getState() instanceof org.bukkit.inventory.InventoryHolder h
                ? h : null;
            return new org.bukkit.event.world.LootGenerateEvent(world, who, holder, table, builder.build(),
                stacks(f.text("loot")), false);
        }, (e, f) -> f.put("loot", lines(((org.bukkit.event.world.LootGenerateEvent) e).getLoot())));
        EventBridge.map("player.loot_replenish", com.destroystokyo.paper.loottable.LootableInventoryReplenishEvent.class,
            f -> {
                Player who = f.player();
                if (who == null || !(f.block().getState() instanceof com.destroystokyo.paper.loottable.LootableInventory
                    inventory)) {
                    return null;
                }
                com.destroystokyo.paper.loottable.LootableInventoryReplenishEvent event =
                    new com.destroystokyo.paper.loottable.LootableInventoryReplenishEvent(who, inventory);
                event.setCancelled(!f.flag("can_loot"));
                return event;
            }, (e, f) -> f.put("can_loot", !((com.destroystokyo.paper.loottable.LootableInventoryReplenishEvent) e)
                .isCancelled()));
    }
}
