package org.basaltmc.stratum.bukkit;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.level.ServerPlayer;

/** The session-less players plugins build themselves, drawn natively and ticked from here. */
public final class FakePlayers {
    private static final Map<UUID, ServerPlayer> kByUuid = new ConcurrentHashMap<>();
    private static volatile boolean kFailed;

    private FakePlayers() {}

    public static ServerPlayer of(UUID id) {
        return kByUuid.isEmpty() || id == null ? null : kByUuid.get(id);
    }

    public static ServerPlayer of(String id) {
        if (kByUuid.isEmpty() || id == null) {
            return null;
        }
        try {
            return kByUuid.get(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static org.bukkit.entity.Entity entity(int entityId) {
        if (kByUuid.isEmpty()) {
            return null;
        }
        for (ServerPlayer player : kByUuid.values()) {
            if (player.getId() == entityId) {
                return player.getBukkitEntity();
            }
        }
        return null;
    }

    public static void register(ServerPlayer player) {
        kByUuid.put(player.getUUID(), player);
    }

    public static void unregister(ServerPlayer player) {
        kByUuid.remove(player.getUUID(), player);
    }

    public static void tickAll() {
        for (ServerPlayer player : kByUuid.values()) {
            try {
                player.tickFake();
            } catch (Throwable t) {
                if (!kFailed) {
                    kFailed = true;
                    t.printStackTrace();
                    Natives.log(3, "Bukkit", "A fake player failed to tick: " + t);
                }
            }
        }
    }
}
