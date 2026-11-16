// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package org.bukkit.craftbukkit.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.util.Optional;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.basaltmc.stratum.bukkit.facade.ChatJson;
import org.bukkit.ChatColor;

public final class CraftChatMessage {
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

    private CraftChatMessage() {}

    public static ChatFormatting getColor(ChatColor color) {
        for (ChatFormatting format : ChatFormatting.values()) {
            if (format.getChar() == color.getChar()) {
                return format;
            }
        }
        return null;
    }

    public static ChatColor getColor(ChatFormatting format) {
        return ChatColor.getByChar(format.getChar());
    }

    public static Optional<Component> fromStringOrOptional(String message) {
        return Optional.ofNullable(fromStringOrNull(message));
    }

    public static Optional<Component> fromStringOrOptional(String message, boolean keepNewlines) {
        return Optional.ofNullable(fromStringOrNull(message, keepNewlines));
    }

    public static Component fromStringOrNull(String message) {
        return fromStringOrNull(message, false);
    }

    public static Component fromStringOrNull(String message, boolean keepNewlines) {
        return message != null && !message.isEmpty() ? fromString(message, keepNewlines)[0] : null;
    }

    public static Component fromStringOrEmpty(String message) {
        return fromStringOrEmpty(message, false);
    }

    public static Component fromStringOrEmpty(String message, boolean keepNewlines) {
        return fromString(message, keepNewlines)[0];
    }

    public static Component[] fromString(String message) {
        return fromString(message, false);
    }

    public static Component[] fromString(String message, boolean keepNewlines) {
        return fromString(message, keepNewlines, false);
    }

    public static Component[] fromString(String message, boolean keepNewlines, boolean plain) {
        String text = message == null ? "" : message;
        if (keepNewlines) {
            return new Component[] {line(text, plain)};
        }
        String[] lines = text.split("\n", -1);
        Component[] out = new Component[lines.length];
        for (int i = 0; i < lines.length; i++) {
            out[i] = line(lines[i], plain);
        }
        return out;
    }

    private static Component line(String text, boolean plain) {
        if (plain) {
            return Component.literal(text);
        }
        return ChatJson.fromAdventure(LEGACY.deserialize(text));
    }

    public static String toJSON(Component component) {
        return ChatJson.toJson(component).toString();
    }

    public static String toJSONOrNull(Component component) {
        return component == null ? null : toJSON(component);
    }

    public static Component fromJSON(String jsonMessage) throws JsonParseException {
        JsonElement element = JsonParser.parseString(jsonMessage);
        return element == null || element.isJsonNull() ? null : ChatJson.fromJson(element);
    }

    public static Component fromJSONOrNull(String jsonMessage) {
        if (jsonMessage == null) {
            return null;
        }
        try {
            return fromJSON(jsonMessage);
        } catch (RuntimeException invalid) {
            return null;
        }
    }

    public static Component fromJSONOrString(String message) {
        return fromJSONOrString(message, false);
    }

    public static Component fromJSONOrString(String message, boolean keepNewlines) {
        return fromJSONOrString(message, false, keepNewlines);
    }

    public static Component fromJSONOrString(String message, boolean nullable, boolean keepNewlines) {
        return fromJSONOrString(message, nullable, keepNewlines, Integer.MAX_VALUE, false);
    }

    public static Component fromJSONOrString(String message, boolean nullable, boolean keepNewlines, int maxLength,
                                             boolean checkJsonContentLength) {
        String text = message == null ? "" : message;
        if (nullable && text.isEmpty()) {
            return null;
        }
        Component component = fromJSONOrNull(text);
        if (component != null) {
            if (checkJsonContentLength) {
                String content = fromComponent(component);
                String trimmed = trimMessage(content, maxLength);
                if (content != trimmed) {
                    return fromString(trimmed, keepNewlines)[0];
                }
            }
            return component;
        }
        return fromString(trimMessage(text, maxLength), keepNewlines)[0];
    }

    public static String trimMessage(String message, int maxLength) {
        return message != null && message.length() > maxLength ? message.substring(0, maxLength) : message;
    }

    public static String fromComponent(Component component) {
        return component == null ? "" : LEGACY.serialize(ChatJson.toAdventure(component));
    }
}
