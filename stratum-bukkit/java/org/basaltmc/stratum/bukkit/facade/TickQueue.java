package org.basaltmc.stratum.bukkit.facade;

import org.basaltmc.stratum.bukkit.BasaltServer;
import org.basaltmc.stratum.bukkit.Natives;
import org.bukkit.Bukkit;

public final class TickQueue {
    private TickQueue() {}

    public static void run(Runnable task) {
        if (Natives.onPluginThread()) {
            task.run();
        } else {
            ((BasaltServer) Bukkit.getServer()).tickTask(task);
        }
    }
}
