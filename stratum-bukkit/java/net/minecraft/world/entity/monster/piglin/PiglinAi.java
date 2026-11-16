// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.entity.monster.piglin;

import java.util.List;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.ActivityData;
import net.minecraft.world.entity.schedule.Activity;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Adapter: the native brain of this species owns the activity, the memories and every behavior. */
public class PiglinAi {
    public static final int REPELLENT_DETECTION_RANGE_HORIZONTAL = 8;
    public static final int REPELLENT_DETECTION_RANGE_VERTICAL = 4;
    public static final int MAX_TIME_BETWEEN_HUNTS = 120;
    public static boolean isWearingSafeArmor(LivingEntity wearer) {
        for (net.minecraft.world.entity.EquipmentSlot slot : net.minecraft.world.entity.EquipmentSlotGroup.ARMOR) {
            if (wearer.getItemBySlot(slot).is(ItemTags.PIGLIN_SAFE_ARMOR)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isZombified(Entity entity) {
        return entity.getType() == EntityTypes.ZOMBIFIED_PIGLIN;
    }

    public static List<ActivityData<Piglin>> getActivities(Piglin piglin) {
        return List.of(
            ActivityData.<Piglin>nativeOnly(Activity.CORE),
            ActivityData.<Piglin>nativeOnly(Activity.IDLE),
            ActivityData.<Piglin>nativeOnly(Activity.FIGHT),
            ActivityData.<Piglin>nativeOnly(Activity.CELEBRATE),
            ActivityData.<Piglin>nativeOnly(Activity.ADMIRE_ITEM),
            ActivityData.<Piglin>nativeOnly(Activity.AVOID),
            ActivityData.<Piglin>nativeOnly(Activity.RIDE));
    }

    protected static void updateActivity(Piglin body) {
    }

    protected static void initMemories(Piglin body, RandomSource random) {
    }
    public static final Item BARTERING_ITEM = null;
    protected static final UniformInt TIME_BETWEEN_HUNTS = null;
    protected static void pickUpItem(ServerLevel level, Piglin body, ItemEntity itemEntity) {
    }
    protected static void stopHoldingOffHandItem(ServerLevel level, Piglin body, boolean barteringEnabled) {
    }
    protected static void cancelAdmiring(ServerLevel level, Piglin body) {
    }
    protected static boolean wantsToPickup(Piglin body, ItemStack itemStack) {
        return false;
    }
    protected static boolean isLovedItem(ItemStack itemStack) {
        return false;
    }
    public static void angerNearbyPiglins(ServerLevel level, Player player, boolean onlyIfTheySeeThePlayer) {
    }
    public static InteractionResult mobInteract(ServerLevel level, Piglin body, Player player, InteractionHand hand) {
        throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.entity.monster.piglin.PiglinAi#mobInteract");
    }
    protected static boolean canAdmire(Piglin body, ItemStack playerHeldItemStack) {
        return false;
    }
    protected static void wasHurtBy(ServerLevel level, Piglin body, LivingEntity attacker) {
    }
    protected static void maybeRetaliate(ServerLevel level, AbstractPiglin body, LivingEntity attacker) {
    }
    public static Optional<SoundEvent> getSoundForCurrentActivity(Piglin body) {
        return java.util.Optional.empty();
    }
    protected static List<AbstractPiglin> getVisibleAdultPiglins(Piglin body) {
        return java.util.List.of();
    }
    protected static void broadcastAngerTarget(ServerLevel level, AbstractPiglin body, LivingEntity target) {
    }
    protected static void broadcastUniversalAnger(ServerLevel level, AbstractPiglin body) {
    }
    protected static void setAngerTarget(ServerLevel level, AbstractPiglin body, LivingEntity target) {
    }
    public static Optional<LivingEntity> getAvoidTarget(Piglin body) {
        return java.util.Optional.empty();
    }
    public static Optional<Player> getNearestVisibleTargetablePlayer(AbstractPiglin body) {
        return java.util.Optional.empty();
    }
    protected static void dontKillAnyMoreHoglinsForAWhile(AbstractPiglin body) {
    }
    protected static boolean isIdle(AbstractPiglin body) {
        return false;
    }
    public static boolean isPlayerHoldingLovedItem(LivingEntity entity) {
        return false;
    }
    public static List<AbstractPiglin> findNearbyAdultPiglins(Brain<?> brain) {
        return java.util.List.of();
    }
}
