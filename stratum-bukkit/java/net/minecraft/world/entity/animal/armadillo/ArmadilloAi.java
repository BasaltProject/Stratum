// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.entity.animal.armadillo;

import java.util.List;
import net.minecraft.world.entity.ai.ActivityData;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.AnimalPanic;
import net.minecraft.world.entity.ai.behavior.Behavior;

/** Adapter: the native brain of this species owns the activity, the memories and every behavior. */
public class ArmadilloAi {
    protected static List<ActivityData<Armadillo>> getActivities() {
        return List.of(
            ActivityData.<Armadillo>nativeOnly(Activity.CORE),
            ActivityData.<Armadillo>nativeOnly(Activity.IDLE),
            ActivityData.<Armadillo>nativeOnly(Activity.PANIC));
    }

    public static void updateActivity(Armadillo body) {
    }
    public static class ArmadilloBallUp extends Behavior<Armadillo> {
        public ArmadilloBallUp() {
            super((java.util.Map) null);
        }
        protected void tick(ServerLevel level, Armadillo body, long timestamp) {
            
        }
        protected boolean checkExtraStartConditions(ServerLevel level, Armadillo body) {
            return false;
        }
        protected boolean canStillUse(ServerLevel level, Armadillo body, long timestamp) {
            return false;
        }
        protected void start(ServerLevel level, Armadillo body, long timestamp) {
            
        }
        protected void stop(ServerLevel level, Armadillo body, long timestamp) {
            
        }
    }
    public static class ArmadilloPanic extends AnimalPanic<Armadillo> {
        public ArmadilloPanic(float speedMultiplier) {
            super((float) 0);
        }
        protected void start(ServerLevel level, Armadillo armadillo, long timestamp) {
            
        }
    }
}
