// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.entity.boss.enderdragon.phases;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
public class DragonTakeoffPhase extends AbstractDragonPhaseInstance {
    public DragonTakeoffPhase(EnderDragon dragon) {
        super(dragon);
    }
    public EnderDragonPhase<DragonTakeoffPhase> getPhase() {
        return EnderDragonPhase.TAKEOFF;
    }
    public void doServerTick(ServerLevel level) {
        super.doServerTick(level);
    }
    public void begin() {
        super.begin();
    }
    public Vec3 getFlyTargetLocation() {
        return super.getFlyTargetLocation();
    }
}
