package org.basaltmc.stratum.bukkit.entity.ops;

import org.basaltmc.stratum.bukkit.entity.Cored;

public interface StorageMinecartOps extends org.bukkit.entity.minecart.StorageMinecart, Cored {
    default org.bukkit.inventory.Inventory getInventory() {
        return ContainerEntityOps.of(core(), this);
    }
}
