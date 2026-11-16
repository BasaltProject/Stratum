// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.entity.boss.enderdragon.phases;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
public class DragonLandingPhase extends AbstractDragonPhaseInstance {
    public DragonLandingPhase(EnderDragon dragon) {
        super(dragon);
    }
    public EnderDragonPhase<DragonLandingPhase> getPhase() {
        return EnderDragonPhase.LANDING;
    }
    public void doClientTick() {
        super.doClientTick();
    }
    public void doServerTick(ServerLevel level) {
        super.doServerTick(level);
    }
    public float getFlySpeed() {
        return super.getFlySpeed();
    }
    public float getTurnSpeed() {
        return super.getTurnSpeed();
    }
    public void begin() {
        super.begin();
    }
    public Vec3 getFlyTargetLocation() {
        return super.getFlyTargetLocation();
    }
}
