// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.entity.boss.enderdragon.phases;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
public class DragonHoverPhase extends AbstractDragonPhaseInstance {
    public DragonHoverPhase(EnderDragon dragon) {
        super(dragon);
    }
    public EnderDragonPhase<DragonHoverPhase> getPhase() {
        return EnderDragonPhase.HOVERING;
    }
}
