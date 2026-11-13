// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.entity;
public interface Attackable {
    default LivingEntity getLastAttacker() {
        return null;
    }
}
