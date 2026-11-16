// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.entity.animal.camel;

import java.util.List;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.ActivityData;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.AnimalPanic;
import net.minecraft.world.entity.ai.behavior.Behavior;

/** Adapter: the native brain of this species owns the activity, the memories and every behavior. */
public class CamelAi {
    protected static List<ActivityData<Camel>> getActivities() {
        return List.of(
            ActivityData.<Camel>nativeOnly(Activity.CORE),
            ActivityData.<Camel>nativeOnly(Activity.IDLE));
    }

    public static void updateActivity(Camel body) {
    }

    protected static void initMemories(Camel body, RandomSource random) {
    }
    public static class CamelPanic extends AnimalPanic<Camel> {
        public CamelPanic(float speedMultiplier) {
            super((float) 0);
        }
        protected boolean checkExtraStartConditions(ServerLevel level, Camel body) {
            return false;
        }
        protected void start(ServerLevel level, Camel camel, long timestamp) {
            
        }
    }
    public static class RandomSitting extends Behavior<Camel> {
        public RandomSitting(int minimalPoseTimeSec) {
            super((java.util.Map) null);
        }
        protected boolean checkExtraStartConditions(ServerLevel level, Camel body) {
            return false;
        }
        protected void start(ServerLevel level, Camel body, long timestamp) {
            
        }
    }
}
