package org.bukkit.craftbukkit.entity;

import java.util.UUID;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.basaltmc.stratum.bukkit.BasaltPlayer;
import org.basaltmc.stratum.bukkit.net.ChannelBridge;

public class CraftPlayer extends BasaltPlayer {
    private ServerPlayer handle;
    private Connection connection;

    public CraftPlayer(UUID id, String name) {
        super(id, name);
    }

    public synchronized void bindLoginChannel(String hostAndPort) {
        if (!ChannelBridge.active()) {
            return;
        }
        getHandle();
        connection.bindChannel(ChannelBridge.channelAt(hostAndPort));
    }

    public synchronized ServerPlayer getHandle() {
        if (handle == null) {
            connection = new Connection(ChannelBridge.channelOf(getUniqueId()));
            handle = new ServerPlayer(new ServerGamePacketListenerImpl(connection));
            handle.bindPlayer(this);
        } else if (connection.getChannel() == null) {
            connection.bindChannel(ChannelBridge.channelOf(getUniqueId()));
        }
        return handle;
    }
}
