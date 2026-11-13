// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.util;

import net.minecraft.network.chat.Component;
public interface ProgressListener {
    void progressStartNoAbort(Component string);
    void progressStart(Component string);
    void progressStage(Component string);
    void progressStagePercentage(int i);
    void stop();
}
