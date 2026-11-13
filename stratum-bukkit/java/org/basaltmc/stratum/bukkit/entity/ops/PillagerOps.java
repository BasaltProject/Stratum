package org.basaltmc.stratum.bukkit.entity.ops;

import org.basaltmc.stratum.bukkit.entity.Cored;

public interface PillagerOps extends org.bukkit.entity.Pillager, Cored {
    default org.bukkit.inventory.Inventory getInventory() {
        return new org.basaltmc.stratum.bukkit.inv.EntityContainerInventory(core(), this, 5, "Inventory");
    }
}
