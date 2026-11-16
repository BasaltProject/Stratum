// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class ItemCooldowns {
    private final org.bukkit.entity.HumanEntity owner;
    private final java.util.Map<Identifier, CooldownInstance> cooldowns = new java.util.HashMap<>();
    private int tickCount;

    public ItemCooldowns() {
        this(null);
    }

    public ItemCooldowns(org.bukkit.entity.HumanEntity owner) {
        this.owner = owner;
    }

    private static org.bukkit.inventory.ItemStack mirror(ItemStack item) {
        return org.bukkit.craftbukkit.inventory.CraftItemStack.asCraftMirror(item);
    }

    public boolean isOnCooldown(ItemStack item) {
        return getCooldownPercent(item, 0.0F) > 0.0F;
    }

    public float getCooldownPercent(ItemStack item, float a) {
        if (owner != null) {
            return owner.hasCooldown(mirror(item)) ? 1.0F : 0.0F;
        }
        CooldownInstance window = cooldowns.get(getCooldownGroup(item));
        if (window == null || tickCount >= window.endTime) {
            return 0.0F;
        }
        return Math.min((window.endTime - (tickCount + a)) / (float) (window.endTime - window.startTime), 1.0F);
    }

    public void tick() {
        tickCount++;
        cooldowns.entrySet().removeIf(entry -> {
            boolean ended = tickCount >= entry.getValue().endTime;
            if (ended) {
                onCooldownEnded(entry.getKey());
            }
            return ended;
        });
    }

    public Identifier getCooldownGroup(ItemStack item) {
        return BuiltInRegistries.ITEM.getKey(item.getItem());
    }

    public void addCooldown(ItemStack item, int time) {
        if (owner != null) {
            owner.setCooldown(mirror(item), time);
        } else {
            addCooldown(getCooldownGroup(item), time);
        }
    }

    public void addCooldown(Identifier cooldownGroup, int time) {
        if (owner != null) {
            owner.setCooldown(net.kyori.adventure.key.Key.key(cooldownGroup.toString()), time);
            return;
        }
        cooldowns.put(cooldownGroup, new CooldownInstance(tickCount, tickCount + time));
        onCooldownStarted(cooldownGroup, time);
    }

    public void removeCooldown(Identifier cooldownGroup) {
        if (owner != null) {
            owner.setCooldown(net.kyori.adventure.key.Key.key(cooldownGroup.toString()), 0);
        } else if (cooldowns.remove(cooldownGroup) != null) {
            onCooldownEnded(cooldownGroup);
        }
    }

    protected void onCooldownStarted(Identifier cooldownGroup, int duration) {
        return;
    }

    protected void onCooldownEnded(Identifier cooldownGroup) {
        return;
    }
    static record CooldownInstance(int startTime, int endTime) {
    }
}
