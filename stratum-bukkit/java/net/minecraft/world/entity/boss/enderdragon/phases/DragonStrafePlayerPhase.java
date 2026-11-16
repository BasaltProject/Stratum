// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.entity.boss.enderdragon.phases;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
public class DragonStrafePlayerPhase extends AbstractDragonPhaseInstance {
    public DragonStrafePlayerPhase(EnderDragon dragon) {
        super(dragon);
    }
    public EnderDragonPhase<DragonStrafePlayerPhase> getPhase() {
        return EnderDragonPhase.STRAFE_PLAYER;
    }
    public void doServerTick(ServerLevel level) {
        super.doServerTick(level);
    }
    public void begin() {
        super.begin();
    }
    public void setTarget(LivingEntity target) {
    }
    public Vec3 getFlyTargetLocation() {
        return super.getFlyTargetLocation();
    }
}
