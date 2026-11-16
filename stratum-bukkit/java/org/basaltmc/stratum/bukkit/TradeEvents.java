package org.basaltmc.stratum.bukkit;

import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

final class TradeEvents {
    private TradeEvents() {}

    static MerchantRecipe recipe(EventBridge.Fields f) {
        ItemStack result = PlayerEvents.stackOf(f.text("result"), f.whole("result_count"));
        ItemStack first = PlayerEvents.stackOf(f.text("cost_a"), f.whole("cost_a_count"));
        if (result == null || first == null) {
            return null;
        }
        MerchantRecipe recipe = new MerchantRecipe(result, (int) f.whole("uses"), (int) f.whole("max_uses"), true,
            (int) f.whole("experience"), (float) f.real("price_multiplier"), (int) f.whole("demand"),
            (int) f.whole("special_price"));
        recipe.addIngredient(first);
        ItemStack second = PlayerEvents.stackOf(f.text("cost_b"), f.whole("cost_b_count"));
        if (second != null) {
            recipe.addIngredient(second);
        }
        return recipe;
    }

    static void write(MerchantRecipe recipe, EventBridge.Fields f) {
        List<ItemStack> ingredients = recipe.getIngredients();
        put(f, "cost_a", "cost_a_count", ingredients.isEmpty() ? null : ingredients.get(0));
        put(f, "cost_b", "cost_b_count", ingredients.size() < 2 ? null : ingredients.get(1));
        put(f, "result", "result_count", recipe.getResult());
        f.put("uses", (long) recipe.getUses());
        f.put("max_uses", (long) recipe.getMaxUses());
        f.put("experience", (long) recipe.getVillagerExperience());
        f.put("price_multiplier", (double) recipe.getPriceMultiplier());
        f.put("demand", (long) recipe.getDemand());
        f.put("special_price", (long) recipe.getSpecialPrice());
    }

    private static void put(EventBridge.Fields f, String item, String count, ItemStack stack) {
        boolean empty = stack == null || stack.getType().isAir() || stack.getAmount() <= 0;
        f.put(item, empty ? "" : stack.getType().getKey().toString());
        f.put(count, empty ? 0L : (long) stack.getAmount());
    }

    private static org.bukkit.entity.AbstractVillager villager(EventBridge.Fields f, String idField) {
        Entity entity = BasaltServer.instance().entityById(f.world(), (int) f.whole(idField));
        return entity instanceof org.bukkit.entity.AbstractVillager villager ? villager : null;
    }

    static void install() {
        EventBridge.map("entity.villager_acquire_trade", org.bukkit.event.entity.VillagerAcquireTradeEvent.class, f -> {
            org.bukkit.entity.AbstractVillager villager = villager(f, "entity_id");
            MerchantRecipe recipe = recipe(f);
            return villager == null || recipe == null ? null
                : new org.bukkit.event.entity.VillagerAcquireTradeEvent(villager, recipe);
        }, (e, f) -> write(((org.bukkit.event.entity.VillagerAcquireTradeEvent) e).getRecipe(), f));
        EventBridge.map("entity.villager_replenish_trade", org.bukkit.event.entity.VillagerReplenishTradeEvent.class,
            f -> {
                org.bukkit.entity.AbstractVillager villager = villager(f, "entity_id");
                MerchantRecipe recipe = recipe(f);
                return villager == null || recipe == null ? null
                    : new org.bukkit.event.entity.VillagerReplenishTradeEvent(villager, recipe);
            });
        EventBridge.map("player.trade", io.papermc.paper.event.player.PlayerTradeEvent.class, f -> {
            org.bukkit.entity.AbstractVillager villager = f.player() == null ? null
                : villager(f, "villager_id");
            MerchantRecipe recipe = recipe(f);
            return villager == null || recipe == null ? null : new io.papermc.paper.event.player.PlayerTradeEvent(
                f.player(), villager, recipe, f.flag("reward_exp"), f.flag("increase_uses"));
        }, (e, f) -> {
            io.papermc.paper.event.player.PlayerTradeEvent event = (io.papermc.paper.event.player.PlayerTradeEvent) e;
            write(event.getTrade(), f);
            f.put("reward_exp", event.isRewardingExp());
            f.put("increase_uses", event.willIncreaseTradeUses());
        });
    }
}
