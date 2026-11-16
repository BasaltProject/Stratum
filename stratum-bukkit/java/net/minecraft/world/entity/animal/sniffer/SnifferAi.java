// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.entity.animal.sniffer;

import java.util.List;
import net.minecraft.world.entity.ai.ActivityData;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;

/** Adapter: the native brain of this species owns the activity, the memories and every behavior. */
public class SnifferAi {
    public static List<ActivityData<Sniffer>> getActivities() {
        return List.of(
            ActivityData.<Sniffer>nativeOnly(Activity.CORE),
            ActivityData.<Sniffer>nativeOnly(Activity.SNIFF),
            ActivityData.<Sniffer>nativeOnly(Activity.DIG),
            ActivityData.<Sniffer>nativeOnly(Activity.IDLE));
    }

    public static void updateActivity(Sniffer body) {
    }
    static class Sniffing extends Behavior<Sniffer> {
        private Sniffing(int min, int max) {
            super((java.util.Map) null);
        }
        protected boolean checkExtraStartConditions(ServerLevel level, Sniffer body) {
            return false;
        }
        protected boolean canStillUse(ServerLevel level, Sniffer body, long timestamp) {
            return false;
        }
        protected void start(ServerLevel level, Sniffer sniffer, long timestamp) {
            
        }
        protected void stop(ServerLevel level, Sniffer sniffer, long timestamp) {
            
        }
    }
    static class Searching extends Behavior<Sniffer> {
        private Searching() {
            super((java.util.Map) null);
        }
        protected boolean checkExtraStartConditions(ServerLevel level, Sniffer sniffer) {
            return false;
        }
        protected boolean canStillUse(ServerLevel level, Sniffer sniffer, long timestamp) {
            return false;
        }
        protected void start(ServerLevel level, Sniffer sniffer, long timestamp) {
            
        }
        protected void stop(ServerLevel level, Sniffer sniffer, long timestamp) {
            
        }
    }
    static class Digging extends Behavior<Sniffer> {
        private Digging(int min, int max) {
            super((java.util.Map) null);
        }
        protected boolean checkExtraStartConditions(ServerLevel level, Sniffer sniffer) {
            return false;
        }
        protected boolean canStillUse(ServerLevel level, Sniffer sniffer, long timestamp) {
            return false;
        }
        protected void start(ServerLevel level, Sniffer sniffer, long timestamp) {
            
        }
        protected void stop(ServerLevel level, Sniffer sniffer, long timestamp) {
            
        }
    }
    static class FinishedDigging extends Behavior<Sniffer> {
        private FinishedDigging(int duration) {
            super((java.util.Map) null);
        }
        protected boolean checkExtraStartConditions(ServerLevel level, Sniffer sniffer) {
            return false;
        }
        protected boolean canStillUse(ServerLevel level, Sniffer sniffer, long timestamp) {
            return false;
        }
        protected void start(ServerLevel level, Sniffer sniffer, long timestamp) {
            
        }
        protected void stop(ServerLevel level, Sniffer sniffer, long timestamp) {
            
        }
    }
    static class FeelingHappy extends Behavior<Sniffer> {
        private FeelingHappy(int min, int max) {
            super((java.util.Map) null);
        }
        protected boolean canStillUse(ServerLevel level, Sniffer sniffer, long timestamp) {
            return false;
        }
        protected void start(ServerLevel level, Sniffer sniffer, long timestamp) {
            
        }
        protected void stop(ServerLevel level, Sniffer sniffer, long timestamp) {
            
        }
    }
    static class Scenting extends Behavior<Sniffer> {
        private Scenting(int min, int max) {
            super((java.util.Map) null);
        }
        protected boolean checkExtraStartConditions(ServerLevel level, Sniffer sniffer) {
            return false;
        }
        protected boolean canStillUse(ServerLevel level, Sniffer sniffer, long timestamp) {
            return false;
        }
        protected void start(ServerLevel level, Sniffer sniffer, long timestamp) {
            
        }
        protected void stop(ServerLevel level, Sniffer sniffer, long timestamp) {
            
        }
    }
}
