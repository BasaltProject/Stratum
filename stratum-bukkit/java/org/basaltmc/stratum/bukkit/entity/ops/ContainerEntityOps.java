package org.basaltmc.stratum.bukkit.entity.ops;

public final class ContainerEntityOps {
    private ContainerEntityOps() {}

    public static org.bukkit.inventory.Inventory of(org.basaltmc.stratum.bukkit.entity.EntityCore core,
            org.bukkit.entity.Entity holder) {
        return new org.basaltmc.stratum.bukkit.inv.EntityContainerInventory(core, holder,
            core.type == org.bukkit.entity.EntityType.HOPPER_MINECART ? 5 : 27);
    }
}
