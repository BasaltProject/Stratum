package org.basaltmc.stratum.bukkit;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/** Turns what chat listeners decided (format, recipients, renderer) into the fields the native chat reads. */
final class ChatRouting {
    private static final String DEFAULT_FORMAT = "<%1$s> %2$s";
    private static final char PLAYER = '\uE000';
    private static final char MESSAGE = '\uE001';

    private ChatRouting() {}

    static void write(AsyncPlayerChatEvent chat, EventBridge.Fields f) {
        if (!DEFAULT_FORMAT.equals(chat.getFormat())) {
            f.put("format", format(chat.getFormat(), chat.getPlayer().getDisplayName()));
        }
        narrow(f, recipients(chat.getRecipients()));
    }

    static void write(io.papermc.paper.event.player.AsyncChatEvent chat, EventBridge.Fields f) {
        Player source = chat.getPlayer();
        if (!(chat.renderer() instanceof io.papermc.paper.chat.ChatRenderer.Default)) {
            Component line = chat.renderer().render(source, source.displayName(), chat.message(),
                BasaltServer.instance().getConsoleSender());
            f.put("format", Text.mini(line));
        }
        Set<Player> players = new HashSet<>();
        for (Audience viewer : chat.viewers()) {
            if (viewer instanceof Player player) {
                players.add(player);
            }
        }
        narrow(f, recipients(players));
    }

    private static void narrow(EventBridge.Fields f, String chosen) {
        if (!"*".equals(chosen) || f.text("recipients").isEmpty()) {
            f.put("recipients", chosen);
        }
    }

    /** Bukkit's String.format layout (%1$s name, %2$s message) as the MiniMessage line the native chat fills in. */
    static String format(String layout, String displayName) {
        StringBuilder out = new StringBuilder();
        int implicit = 0;
        for (int i = 0; i < layout.length(); i++) {
            char c = layout.charAt(i);
            if (c != '%' || i + 1 >= layout.length()) {
                out.append(c);
                continue;
            }
            if (layout.startsWith("%%", i)) {
                out.append('%');
                i++;
            } else if (layout.startsWith("%1$s", i)) {
                out.append(PLAYER);
                i += 3;
            } else if (layout.startsWith("%2$s", i)) {
                out.append(MESSAGE);
                i += 3;
            } else if (layout.startsWith("%s", i)) {
                out.append(implicit++ == 0 ? PLAYER : MESSAGE);
                i++;
            } else {
                out.append(c);
            }
        }
        String mini = Text.mini(out.toString());
        return mini.replace(String.valueOf(PLAYER), Text.mini(displayName)).replace(String.valueOf(MESSAGE), "{message}");
    }

    static String recipients(Set<Player> chosen) {
        Set<UUID> online = BasaltServer.instance().getOnlinePlayers().stream().map(Player::getUniqueId)
            .collect(Collectors.toSet());
        Set<UUID> wanted = chosen.stream().map(Player::getUniqueId).collect(Collectors.toSet());
        if (wanted.equals(online)) {
            return "*";
        }
        return wanted.stream().map(UUID::toString).collect(Collectors.joining(","));
    }
}
