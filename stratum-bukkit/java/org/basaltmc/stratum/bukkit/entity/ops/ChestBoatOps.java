package org.basaltmc.stratum.bukkit.entity.ops;

import org.basaltmc.stratum.bukkit.entity.Cored;

public interface ChestBoatOps extends org.bukkit.entity.ChestBoat, Cored {
    default org.bukkit.inventory.Inventory getInventory() {
        return ContainerEntityOps.of(core(), this);
    }
}
