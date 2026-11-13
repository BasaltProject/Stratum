// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.util.profiling;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
public interface ProfilerPathEntry {
    long getDuration();
    long getMaxDuration();
    long getCount();
    Object2LongMap<String> getCounters();
}
