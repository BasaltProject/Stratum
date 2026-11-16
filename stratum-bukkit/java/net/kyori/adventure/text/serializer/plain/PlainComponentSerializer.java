package net.kyori.adventure.text.serializer.plain;

import net.kyori.adventure.text.Component;

/** The adventure 4 name for the plain serializer, kept for plugins built against it. */
public final class PlainComponentSerializer {
    private static final PlainComponentSerializer kInstance = new PlainComponentSerializer();

    private PlainComponentSerializer() {
    }

    public static PlainComponentSerializer plain() {
        return kInstance;
    }

    public String serialize(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }
}
