// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.entity.boss.enderdragon.phases;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
public class DragonHoverPhase extends AbstractDragonPhaseInstance {
    public DragonHoverPhase(EnderDragon dragon) {
        super(dragon);
    }
    public EnderDragonPhase<DragonHoverPhase> getPhase() {
        return EnderDragonPhase.HOVERING;
    }
    public void doServerTick(ServerLevel level) {
        super.doServerTick(level);
    }
    public boolean isSitting() {
        return super.isSitting();
    }
    public void begin() {
        super.begin();
    }
    public float getFlySpeed() {
        return super.getFlySpeed();
    }
    public Vec3 getFlyTargetLocation() {
        return super.getFlyTargetLocation();
    }
}
