package org.basaltmc.stratum.bukkit.entity.ops;

import org.basaltmc.stratum.bukkit.entity.Cored;
import org.bukkit.entity.Snowman;

public interface SnowmanOps extends Snowman, Cored {
    default boolean isDerp() {
        return !core().nbtFlag("Pumpkin");
    }

    default void setDerp(boolean derp) {
        core().nbtSet("Pumpkin", "bool", !derp);
    }
}
