package org.basaltmc.stratum.bukkit.entity.ops;

import org.basaltmc.stratum.bukkit.entity.Cored;

public interface AbstractWindChargeOps extends org.bukkit.entity.AbstractWindCharge, Cored {
    default void explode() {
        core().call("land");
    }
}
