package org.basaltmc.stratum.bukkit.entity.ops;

import org.basaltmc.stratum.bukkit.entity.Cored;
import org.bukkit.entity.ZombieNautilus;

public interface ZombieNautilusOps extends ZombieNautilus, Cored {
    default ZombieNautilus.Variant getVariant() {
        return EntityNbt.variant(core(), "variant", io.papermc.paper.registry.RegistryKey.ZOMBIE_NAUTILUS_VARIANT,
            "temperate");
    }

    default void setVariant(ZombieNautilus.Variant variant) {
        EntityNbt.variant(core(), "variant", variant);
    }
}
