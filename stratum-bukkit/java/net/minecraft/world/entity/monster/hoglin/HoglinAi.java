// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.entity.monster.hoglin;

import java.util.List;
import net.minecraft.world.entity.ai.ActivityData;
import net.minecraft.world.entity.schedule.Activity;

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
}
