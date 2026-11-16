// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.entity.boss.enderdragon.phases;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
public class DragonChargePlayerPhase extends AbstractDragonPhaseInstance {
    public DragonChargePlayerPhase(EnderDragon dragon) {
        super(dragon);
    }
    public EnderDragonPhase<DragonChargePlayerPhase> getPhase() {
        return EnderDragonPhase.CHARGING_PLAYER;
    }
    public void doServerTick(ServerLevel level) {
        super.doServerTick(level);
    }
    public void begin() {
        super.begin();
    }
    public void setTarget(Vec3 target) {
    }
    public float getFlySpeed() {
        return super.getFlySpeed();
    }
    public Vec3 getFlyTargetLocation() {
        return super.getFlyTargetLocation();
    }
}
