package org.bukkit.craftbukkit;

import java.io.File;
import java.util.Map;
import net.minecraft.server.dedicated.DedicatedServer;
import org.basaltmc.stratum.bukkit.BasaltServer;
import org.bukkit.World;

/** The class plugins find behind Bukkit.getServer(); its package name is how they recognise a CraftBukkit server. */
public final class CraftServer extends BasaltServer {
    private final DedicatedServer console = DedicatedServer.instance();
    private org.bukkit.command.CommandMap commandMap;
    private final Map<String, World> worlds = new java.util.concurrent.ConcurrentHashMap<>();

    public CraftServer(File root, File pluginsFolder) {
        super(root, pluginsFolder);
        this.commandMap = getCommandMap();
    }

    @Override
    public org.bukkit.command.CommandMap getCommandMap() {
        return super.getCommandMap();
    }

    @Override
    public String getMinecraftVersion() {
        return super.getMinecraftVersion();
    }

    public net.minecraft.server.dedicated.DedicatedPlayerList getHandle() {
        return (net.minecraft.server.dedicated.DedicatedPlayerList) console.getPlayerList();
    }

    public DedicatedServer getServer() {
        return console;
    }

    private final org.bukkit.craftbukkit.metadata.EntityMetadataStore entityMetadata =
        new org.bukkit.craftbukkit.metadata.EntityMetadataStore();

    public org.bukkit.craftbukkit.metadata.EntityMetadataStore getEntityMetadata() {
        return entityMetadata;
    }
}
