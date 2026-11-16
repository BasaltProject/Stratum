// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.entity.boss.enderdragon.phases;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.server.level.ServerLevel;
public class DragonSittingScanningPhase extends AbstractDragonSittingPhase {
    public DragonSittingScanningPhase(EnderDragon dragon) {
        super(dragon);
    }
    public EnderDragonPhase<DragonSittingScanningPhase> getPhase() {
        return EnderDragonPhase.SITTING_SCANNING;
    }
    public void doServerTick(ServerLevel level) {
        super.doServerTick(level);
    }
    public void begin() {
        super.begin();
    }
}
