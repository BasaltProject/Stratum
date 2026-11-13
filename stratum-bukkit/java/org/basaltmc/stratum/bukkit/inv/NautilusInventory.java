package org.basaltmc.stratum.bukkit.inv;

import org.basaltmc.stratum.bukkit.entity.EntityCore;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ArmoredSaddledMountInventory;

public final class NautilusInventory extends MountInventory implements ArmoredSaddledMountInventory {
    public NautilusInventory(EntityCore core, Entity nautilus) {
        super(core, nautilus);
    }
}
