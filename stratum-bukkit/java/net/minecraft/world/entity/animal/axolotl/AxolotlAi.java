// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.entity.animal.axolotl;

import java.util.List;
import net.minecraft.world.entity.ai.ActivityData;
import net.minecraft.world.entity.schedule.Activity;

/** Adapter: the native brain of this species owns the activity, the memories and every behavior. */
public class AxolotlAi {
    protected static List<ActivityData<Axolotl>> getActivities() {
        return List.of(
            ActivityData.<Axolotl>nativeOnly(Activity.PLAY_DEAD),
            ActivityData.<Axolotl>nativeOnly(Activity.FIGHT),
            ActivityData.<Axolotl>nativeOnly(Activity.CORE),
            ActivityData.<Axolotl>nativeOnly(Activity.IDLE));
    }

    public static void updateActivity(Axolotl body) {
    }
    protected static ActivityData<Axolotl> initPlayDeadActivity() {
        throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.entity.animal.axolotl.AxolotlAi#initPlayDeadActivity");
    }
    protected static ActivityData<Axolotl> initFightActivity() {
        throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.entity.animal.axolotl.AxolotlAi#initFightActivity");
    }
    protected static ActivityData<Axolotl> initCoreActivity() {
        throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.entity.animal.axolotl.AxolotlAi#initCoreActivity");
    }
    protected static ActivityData<Axolotl> initIdleActivity() {
        throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.entity.animal.axolotl.AxolotlAi#initIdleActivity");
    }
}
