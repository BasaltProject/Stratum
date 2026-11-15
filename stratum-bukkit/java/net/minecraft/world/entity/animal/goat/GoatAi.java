// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.entity.animal.goat;

import java.util.List;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.ActivityData;
import net.minecraft.world.entity.schedule.Activity;

/** Adapter: the native brain of this species owns the activity, the memories and every behavior. */
public class GoatAi {
    public static final int RAM_PREPARE_TIME = 20;
    public static final int RAM_MAX_DISTANCE = 7;
    public static final int MAX_LONG_JUMP_HEIGHT = 5;
    public static final int MAX_LONG_JUMP_WIDTH = 5;
    public static final float MAX_JUMP_VELOCITY_MULTIPLIER = 3.5714288F;
    public static final int RAM_MIN_DISTANCE = 4;
    public static final float ADULT_RAM_KNOCKBACK_FORCE = 2.5F;
    public static final float BABY_RAM_KNOCKBACK_FORCE = 1.0F;
    protected static List<ActivityData<Goat>> getActivities() {
        return List.of(
            ActivityData.<Goat>nativeOnly(Activity.CORE),
            ActivityData.<Goat>nativeOnly(Activity.IDLE),
            ActivityData.<Goat>nativeOnly(Activity.LONG_JUMP),
            ActivityData.<Goat>nativeOnly(Activity.RAM));
    }

    public static void updateActivity(Goat body) {
    }

    protected static void initMemories(Goat body, RandomSource random) {
    }
}
