// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.entity.boss.enderdragon.phases;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.server.level.ServerLevel;
public class DragonSittingFlamingPhase extends AbstractDragonSittingPhase {
    public DragonSittingFlamingPhase(EnderDragon dragon) {
        super(dragon);
    }
    public EnderDragonPhase<DragonSittingFlamingPhase> getPhase() {
        return EnderDragonPhase.SITTING_FLAMING;
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
    public void end() {
        super.end();
    }
    public void resetFlameCount() {
    }
}
