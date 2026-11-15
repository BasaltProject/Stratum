// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.entity.animal.equine;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
public class Mule extends AbstractChestedHorse {
    public Mule(EntityType<? extends Mule> type, Level level) {
        super(type, level);
    }
    protected SoundEvent getAmbientSound() {
        return net.minecraft.sounds.SoundEvents.byId("entity.mule.ambient");
    }
    protected SoundEvent getAngrySound() {
        return net.minecraft.sounds.SoundEvents.MULE_ANGRY;
    }
    protected SoundEvent getDeathSound() {
        return net.minecraft.sounds.SoundEvents.byId("entity.mule.death");
    }
    protected SoundEvent getEatingSound() {
        return net.minecraft.sounds.SoundEvents.MULE_EAT;
    }
    protected SoundEvent getHurtSound(DamageSource source) {
        return net.minecraft.sounds.SoundEvents.byId("entity.mule.hurt");
    }
    protected void playJumpSound() {
        }
    protected void playChestEquipsSound() {
        }
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return (net.minecraft.world.entity.AgeableMob) net.minecraft.world.entity.EntityTypes.MULE.create(level, net.minecraft.world.entity.EntitySpawnReason.BREEDING);
    }
}
