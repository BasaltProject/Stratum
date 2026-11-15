// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.entity.monster.skeleton;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
public class Skeleton extends AbstractSkeleton {
    private int freezeConversionTime;
    public Skeleton(EntityType<? extends Skeleton> type, Level level) {
        super(type, level);
    }
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
    }
    public boolean isShaking() {
        return freezeConversionTime > 0;
    }
    public void tick() {
    }
    protected void addAdditionalSaveData(ValueOutput output) {
    }
    protected void readAdditionalSaveData(ValueInput input) {
    }
    public void startFreezeConversion(int time) {
        this.freezeConversionTime = time;
    }
    public boolean canFreeze() {
        return false;
    }
    protected SoundEvent getAmbientSound() {
        return net.minecraft.sounds.SoundEvents.SKELETON_AMBIENT;
    }
    protected SoundEvent getHurtSound(DamageSource source) {
        return net.minecraft.sounds.SoundEvents.SKELETON_HURT;
    }
    protected SoundEvent getDeathSound() {
        return net.minecraft.sounds.SoundEvents.SKELETON_DEATH;
    }
    protected SoundEvent getStepSound() {
        return net.minecraft.sounds.SoundEvents.SKELETON_STEP;
    }
}
