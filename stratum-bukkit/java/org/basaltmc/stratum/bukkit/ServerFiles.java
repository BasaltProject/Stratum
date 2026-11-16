package org.basaltmc.stratum.bukkit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.basaltmc.stratum.bukkit.fs.PathTranslator;
import org.bukkit.Server;

/** Writes the server.properties and bukkit.yml a Paper server would have, into the shadow root plugins read from. */
final class ServerFiles {
    private ServerFiles() {
    }

    static void write(Server server) {
        Path dir = Path.of(".").toAbsolutePath().normalize().resolve(PathTranslator.SHADOW).resolve("root");
        try {
            Files.createDirectories(dir);
            Path properties = dir.resolve("server.properties");
            if (!Files.exists(properties)) {
                Properties out = new Properties();
                String world = server.getWorlds().isEmpty() ? "world" : server.getWorlds().get(0).getName();
                out.setProperty("level-name", world);
                out.setProperty("level-type", "minecraft\\:normal");
                out.setProperty("motd", String.valueOf(server.getMotd()));
                out.setProperty("max-players", String.valueOf(server.getMaxPlayers()));
                out.setProperty("server-port", String.valueOf(server.getPort()));
                out.setProperty("online-mode", String.valueOf(server.getOnlineMode()));
                out.setProperty("view-distance", String.valueOf(server.getViewDistance()));
                out.setProperty("simulation-distance", String.valueOf(server.getSimulationDistance()));
                out.setProperty("allow-nether", String.valueOf(server.getAllowNether()));
                out.setProperty("allow-flight", String.valueOf(server.getAllowFlight()));
                out.setProperty("hardcore", String.valueOf(server.isHardcore()));
                try (var writer = Files.newBufferedWriter(properties)) {
                    out.store(writer, "Minecraft server properties");
                }
            }
            Path bukkit = dir.resolve("bukkit.yml");
            if (!Files.exists(bukkit)) {
                Files.writeString(bukkit, "settings:\n  allow-end: true\n  warn-on-overload: true\n"
                    + "  world-container: worlds\n  connection-throttle: 4000\n");
            }
        } catch (IOException | RuntimeException ignored) {
            // A plugin that needs these files then reports its own read error.
        }
    }
}
