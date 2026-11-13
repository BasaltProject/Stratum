// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;

public abstract class JumpGoal extends Goal {
    public JumpGoal() {
        setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }
}
