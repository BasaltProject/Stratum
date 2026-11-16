// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.entity.animal.nautilus;

import java.util.List;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.ActivityData;
import net.minecraft.world.entity.schedule.Activity;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.item.ItemStack;

/** Adapter: the native brain of this species owns the activity, the memories and every behavior. */
public class NautilusAi {
    public static List<ActivityData<Nautilus>> getActivities() {
        return List.of(
            ActivityData.<Nautilus>nativeOnly(Activity.CORE),
            ActivityData.<Nautilus>nativeOnly(Activity.IDLE),
            ActivityData.<Nautilus>nativeOnly(Activity.FIGHT));
    }

    public static void updateActivity(Nautilus body) {
    }

    protected static void initMemories(AbstractNautilus body, RandomSource random) {
    }
    protected static final TargetingConditions ATTACK_TARGET_CONDITIONS = null;
    protected static Optional<? extends LivingEntity> findNearestValidAttackTarget(ServerLevel level, AbstractNautilus body) {
        return java.util.Optional.empty();
    }
    protected static void setAngerTarget(ServerLevel level, AbstractNautilus body, LivingEntity target) {
    }
    public static Predicate<ItemStack> getTemptations() {
        throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.entity.animal.nautilus.NautilusAi#getTemptations");
    }
}
