// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.entity.monster.hoglin;

import java.util.List;
import net.minecraft.world.entity.ai.ActivityData;
import net.minecraft.world.entity.schedule.Activity;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;

/** Adapter: the native brain of this species owns the activity, the memories and every behavior. */
public class HoglinAi {
    public static final int REPELLENT_DETECTION_RANGE_HORIZONTAL = 8;
    public static final int REPELLENT_DETECTION_RANGE_VERTICAL = 4;
    protected static List<ActivityData<Hoglin>> getActivities() {
        return List.of(
            ActivityData.<Hoglin>nativeOnly(Activity.CORE),
            ActivityData.<Hoglin>nativeOnly(Activity.IDLE),
            ActivityData.<Hoglin>nativeOnly(Activity.FIGHT),
            ActivityData.<Hoglin>nativeOnly(Activity.AVOID));
    }

    protected static void updateActivity(Hoglin body) {
    }
    protected static void onHitTarget(Hoglin attackerBody, LivingEntity target) {
    }
    static boolean isPosNearNearestRepellent(Hoglin body, BlockPos pos) {
        return false;
    }
    protected static void wasHurtBy(ServerLevel level, Hoglin body, LivingEntity attacker) {
    }
    public static Optional<SoundEvent> getSoundForCurrentActivity(Hoglin body) {
        return java.util.Optional.empty();
    }
    protected static boolean isPacified(Hoglin body) {
        return false;
    }
}
