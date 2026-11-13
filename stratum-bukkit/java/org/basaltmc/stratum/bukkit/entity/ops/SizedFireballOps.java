package org.basaltmc.stratum.bukkit.entity.ops;

import org.basaltmc.stratum.bukkit.entity.Cored;
import org.bukkit.entity.SizedFireball;

public interface SizedFireballOps extends SizedFireball, Cored {
    default org.bukkit.inventory.ItemStack getDisplayItem() {
        return EntityNbt.item(core(), "Item", org.bukkit.Material.FIRE_CHARGE);
    }

    default void setDisplayItem(org.bukkit.inventory.ItemStack item) {
        EntityNbt.storeItem(core(), "Item", item);
    }
}
