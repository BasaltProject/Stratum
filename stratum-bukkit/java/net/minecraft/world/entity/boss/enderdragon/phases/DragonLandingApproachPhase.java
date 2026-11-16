// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.entity.boss.enderdragon.phases;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
public class DragonLandingApproachPhase extends AbstractDragonPhaseInstance {
    public DragonLandingApproachPhase(EnderDragon dragon) {
        super(dragon);
    }
    public EnderDragonPhase<DragonLandingApproachPhase> getPhase() {
        return EnderDragonPhase.LANDING_APPROACH;
    }
    public void begin() {
        super.begin();
    }
    public void doServerTick(ServerLevel level) {
        super.doServerTick(level);
    }
    public Vec3 getFlyTargetLocation() {
        return super.getFlyTargetLocation();
    }
}
