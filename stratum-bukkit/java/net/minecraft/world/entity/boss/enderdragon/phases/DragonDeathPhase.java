// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.entity.boss.enderdragon.phases;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
public class DragonDeathPhase extends AbstractDragonPhaseInstance {
    public DragonDeathPhase(EnderDragon dragon) {
        super(dragon);
    }
    public EnderDragonPhase<DragonDeathPhase> getPhase() {
        return EnderDragonPhase.DYING;
    }
    public void doClientTick() {
        super.doClientTick();
    }
    public void doServerTick(ServerLevel level) {
        super.doServerTick(level);
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
