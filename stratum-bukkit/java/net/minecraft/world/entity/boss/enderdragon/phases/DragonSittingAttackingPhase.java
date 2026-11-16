// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.entity.boss.enderdragon.phases;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.server.level.ServerLevel;
public class DragonSittingAttackingPhase extends AbstractDragonSittingPhase {
    public DragonSittingAttackingPhase(EnderDragon dragon) {
        super(dragon);
    }
    public EnderDragonPhase<DragonSittingAttackingPhase> getPhase() {
        return EnderDragonPhase.SITTING_ATTACKING;
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
}
