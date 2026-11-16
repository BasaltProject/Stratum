// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.entity.boss.enderdragon.phases;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.damagesource.DamageSource;
public abstract class AbstractDragonSittingPhase extends AbstractDragonPhaseInstance {
    public AbstractDragonSittingPhase(EnderDragon dragon) {
        super(dragon);
    }
    public boolean isSitting() {
        return true;
    }
    public float onHurt(DamageSource source, float damage) {
        return super.onHurt(source, damage);
    }
}
