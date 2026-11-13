// Independent reimplementation of the BungeeCord API shape over Basalt natives. See stratum-bungee/NOTICE.md
package org.basaltmc.stratum.bungee;

import java.util.function.BiFunction;

/** Basalt's own hook for text options (prefix, suffix or any key) of players, which BungeeCord has no API for. */
public final class PermissionMeta {
    private static volatile BiFunction<String, String, String> source;

    private PermissionMeta() {}

    /** The source gets a player name (empty for the console) and a key and answers null for none; null removes it. */
    public static void provide(BiFunction<String, String, String> given) {
        source = given;
        Natives.permissionMeta(given != null);
    }

    static String answer(String player, String key) {
        final BiFunction<String, String, String> current = source;
        if (current == null) {
            return null;
        }
        try {
            return current.apply(player, key);
        } catch (final RuntimeException error) {
            Natives.log("a permission meta source failed: " + error);
            return null;
        }
    }
}
