// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.entity.boss.enderdragon.phases;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
public class DragonHoldingPatternPhase extends AbstractDragonPhaseInstance {
    public DragonHoldingPatternPhase(EnderDragon dragon) {
        super(dragon);
    }
    public EnderDragonPhase<DragonHoldingPatternPhase> getPhase() {
        return EnderDragonPhase.HOLDING_PATTERN;
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
    public void onCrystalDestroyed(EndCrystal crystal, BlockPos pos, DamageSource source, Player player) {
        super.onCrystalDestroyed(crystal, pos, source, player);
    }
}
