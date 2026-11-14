package org.basaltmc.stratum.bukkit;

import java.util.ArrayList;
import java.util.List;

/** Loads the classes and compiles the paths the first player would otherwise hit on the tick thread. */
final class Warmup {
    private Warmup() {}

    static void install() {
        Thread thread = new Thread(Warmup::run, "basalt-warmup");
        thread.setDaemon(true);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    private static void run() {
        try {
            preloadCore();
            warmRegistries();
            warmEntities();
            warmCommandList();
            warmChunkWrite();
            warmWorldReads();
            preloadPluginClasses();
        } catch (Throwable ignored) {
        }
    }

    private static void preloadCore() {
        for (String name : new String[] {
                "org.bukkit.event.player.PlayerJoinEvent", "org.bukkit.event.player.PlayerQuitEvent",
                "org.bukkit.event.player.PlayerCommandSendEvent", "org.bukkit.event.player.PlayerCommandPreprocessEvent",
                "org.bukkit.event.player.PlayerMoveEvent", "org.bukkit.event.player.PlayerTeleportEvent",
                "org.bukkit.event.HandlerList", "org.bukkit.event.world.GameEvent",
                "org.bukkit.event.server.ServerCommandEvent", "java.util.regex.Pattern",
                "org.bukkit.craftbukkit.entity.CraftPlayer"}) {
            try {
                Class.forName(name);
            } catch (Throwable ignored) {
            }
        }
    }

    private static void warmRegistries() {
        try {
            org.bukkit.Registry.GAME_EVENT.get(org.bukkit.NamespacedKey.minecraft("step"));
            org.bukkit.Registry.ENTITY_TYPE.get(org.bukkit.NamespacedKey.minecraft("zombie"));
            org.bukkit.Registry.BLOCK.get(org.bukkit.NamespacedKey.minecraft("stone"));
        } catch (Throwable ignored) {
        }
    }

    /** Builds throwaway unspawned mobs so the entity wrapper classes are loaded and compiled before the first event. */
    private static void warmEntities() {
        try {
            List<org.bukkit.World> worlds = org.bukkit.Bukkit.getWorlds();
            if (worlds.isEmpty() || !(worlds.get(0) instanceof BasaltWorld world)) {
                return;
            }
            for (org.bukkit.entity.EntityType type : new org.bukkit.entity.EntityType[] {
                    org.bukkit.entity.EntityType.ZOMBIE, org.bukkit.entity.EntityType.BAT,
                    org.bukkit.entity.EntityType.COW}) {
                org.basaltmc.stratum.bukkit.entity.EntityCore.template(world, type, 0, 0, 0, 0, 0);
            }
        } catch (Throwable ignored) {
        }
    }

    /** Runs the chunk section write-back on a scratch chunk of a world the engine does not have. */
    private static void warmChunkWrite() {
        try {
            List<org.bukkit.World> worlds = org.bukkit.Bukkit.getWorlds();
            if (!worlds.isEmpty() && worlds.get(0) instanceof BasaltWorld world) {
                net.minecraft.world.level.chunk.LevelChunk.warmWriteBack(world.getHandle(), "basalt:warmup");
            }
        } catch (Throwable ignored) {
        }
    }

    /** Calls the read-only world and block wrappers an editor hits first, without loading or changing a chunk. */
    private static void warmWorldReads() {
        try {
            List<org.bukkit.World> worlds = org.bukkit.Bukkit.getWorlds();
            if (worlds.isEmpty()) {
                return;
            }
            org.bukkit.World world = worlds.get(0);
            for (int round = 0; round < 3; round++) {
                world.getMinHeight();
                world.getMaxHeight();
                world.getUID();
                world.getEnvironment();
                world.isChunkLoaded(round, round);
                org.bukkit.block.Block block = world.getBlockAt(round, 64, round);
                block.getType();
                block.getBlockData();
                world.getBlockData(round, 64, round);
            }
        } catch (Throwable ignored) {
        }
    }

    /** Loads, without running, the classes of an enabled plugin that its first heavy edit uses. */
    private static void preloadPluginClasses() {
        try {
            org.bukkit.plugin.Plugin plugin = org.bukkit.Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit");
            if (plugin == null) {
                return;
            }
            ClassLoader loader = plugin.getClass().getClassLoader();
            try (java.io.InputStream in = Warmup.class.getResourceAsStream("/warmup_classes.txt")) {
                if (in == null) {
                    return;
                }
                for (String name : new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8).split("\n")) {
                    if (name.isEmpty()) {
                        continue;
                    }
                    try {
                        Class.forName(name, false, loader);
                    } catch (Throwable ignored) {
                    }
                }
            }
        } catch (Throwable ignored) {
        }
    }

    /** Runs the command list conversion on a synthetic list so its code is compiled before the first join. */
    private static void warmCommandList() {
        StringBuilder joined = new StringBuilder();
        for (int index = 0; index < 600; index++) {
            joined.append(index == 0 ? "" : "\n").append("warm").append(index);
        }
        String text = joined.toString();
        for (int round = 0; round < 3; round++) {
            List<String> names = new ArrayList<>();
            for (String name : text.split("\n")) {
                if (!name.isEmpty()) {
                    names.add(name);
                }
            }
            if (String.join("\n", names).length() != text.length()) {
                return;
            }
        }
    }
}
