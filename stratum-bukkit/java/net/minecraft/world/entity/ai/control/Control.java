// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.entity.ai.control;

import net.minecraft.util.Mth;

public interface Control {
    default float rotateTowards(float fromAngle, float toAngle, float maxRot) {
        return Mth.approachDegrees(fromAngle, toAngle, maxRot);
    }
}
