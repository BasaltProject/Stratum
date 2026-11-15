package org.basaltmc.stratum.bukkit;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.plugin.messaging.PluginMessageListenerRegistration;
import org.bukkit.plugin.messaging.StandardMessenger;

/** Bukkit's messenger, told to the server so that the channels a plugin listens on reach it. */
public final class BasaltMessenger extends StandardMessenger {
    private static final String kRegister = "minecraft:register";
    private static final String kUnregister = "minecraft:unregister";
    private final Map<UUID, Set<String>> clientChannels = new ConcurrentHashMap<>();

    void listenForClientChannels() {
        Natives.listenChannel(kRegister);
        Natives.listenChannel(kUnregister);
    }

    Set<String> clientChannels(UUID player) {
        Set<String> channels = clientChannels.get(player);
        return channels == null ? Set.of() : Collections.unmodifiableSet(channels);
    }

    void forget(UUID player) {
        clientChannels.remove(player);
    }

    private void clientDeclared(org.bukkit.entity.Player source, boolean add, byte[] message) {
        Set<String> known = clientChannels.computeIfAbsent(source.getUniqueId(),
            id -> Collections.synchronizedSet(new TreeSet<>()));
        for (String channel : new String(message, StandardCharsets.UTF_8).split("\0")) {
            if (channel.isEmpty() || !(add ? known.add(channel) : known.remove(channel))) {
                continue;
            }
            org.bukkit.event.Event event = add
                ? new org.bukkit.event.player.PlayerRegisterChannelEvent(source, channel)
                : new org.bukkit.event.player.PlayerUnregisterChannelEvent(source, channel);
            org.bukkit.Bukkit.getPluginManager().callEvent(event);
        }
    }

    @Override
    public PluginMessageListenerRegistration registerIncomingPluginChannel(Plugin plugin, String channel,
            PluginMessageListener listener) {
        PluginMessageListenerRegistration registration = super.registerIncomingPluginChannel(plugin, channel, listener);
        Natives.listenChannel(channel);
        return registration;
    }

    @Override
    public void dispatchIncomingMessage(org.bukkit.entity.Player source, String channel, byte[] message) {
        if (channel.equals(kRegister) || channel.equals(kUnregister)) {
            clientDeclared(source, channel.equals(kRegister), message);
        }
        for (PluginMessageListenerRegistration registration : getIncomingChannelRegistrations(channel)) {
            long started = System.nanoTime();
            try {
                registration.getListener().onPluginMessageReceived(channel, source, message);
            } finally {
                Natives.recordTime(registration.getPlugin().getName(), System.nanoTime() - started);
            }
        }
    }
}
