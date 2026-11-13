package org.basaltmc.stratum.bukkit.entity.ops;

import org.basaltmc.stratum.bukkit.entity.Cored;
import org.bukkit.entity.AbstractNautilus;

public interface AbstractNautilusOps extends AbstractNautilus, Cored {
    default org.bukkit.inventory.ArmoredSaddledMountInventory getInventory() {
        return new org.basaltmc.stratum.bukkit.inv.NautilusInventory(core(), this);
    }
}
