// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.entity.animal.allay;

import java.util.List;
import net.minecraft.world.entity.ai.ActivityData;
import net.minecraft.world.entity.schedule.Activity;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

/** Adapter: the native brain of this species owns the activity, the memories and every behavior. */
public class AllayAi {
    protected static List<ActivityData<Allay>> getActivities() {
        return List.of(
            ActivityData.<Allay>nativeOnly(Activity.CORE),
            ActivityData.<Allay>nativeOnly(Activity.IDLE));
    }

    public static void updateActivity(Allay body) {
    }
    public static void hearNoteblock(LivingEntity allay, BlockPos pos) {
    }
    public static Optional<ServerPlayer> getLikedPlayer(LivingEntity allay) {
        return java.util.Optional.empty();
    }
}
