package org.basaltmc.stratum.bukkit.misc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.basaltmc.stratum.bukkit.BasaltPlayer;
import org.basaltmc.stratum.bukkit.MiscNatives;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

/** Joins Bukkit's permission model to the native chain: injected permissibles answer natively, plugin.yml defaults are declared. */
public final class PermissionBridge {
    private static final int kUndefined = -1;
    private static final Map<String, BasaltPlayer> kForeign = new ConcurrentHashMap<>();
    private static final ThreadLocal<Boolean> kAnswering = new ThreadLocal<>();
    private static final Object kLock = new Object();
    private static boolean kActive;

    private PermissionBridge() {}

    /** Called by the native chain, from any thread: -1 when this adapter has no opinion. */
    public static int providedPermission(String uuid, String node) {
        BasaltPlayer player = kForeign.get(uuid);
        if (player == null || kAnswering.get() != null) {
            return kUndefined;
        }
        kAnswering.set(Boolean.TRUE);
        try {
            Permissible permissible = player.currentPermissible();
            if (permissible instanceof BasaltPermissible || !permissible.isPermissionSet(node)) {
                return kUndefined;
            }
            return permissible.hasPermission(node) ? 1 : 0;
        } catch (Throwable failure) {
            return kUndefined;
        } finally {
            kAnswering.remove();
        }
    }

    /** Notices whether a plugin replaced the player's permissible, and keeps the native provider in step. */
    public static void inspect(BasaltPlayer player) {
        boolean foreign = !(player.currentPermissible() instanceof BasaltPermissible);
        String uuid = player.getUniqueId().toString();
        synchronized (kLock) {
            if (foreign) {
                kForeign.put(uuid, player);
            } else {
                kForeign.remove(uuid);
            }
            setActive(!kForeign.isEmpty());
        }
    }

    public static void forget(BasaltPlayer player) {
        synchronized (kLock) {
            kForeign.remove(player.getUniqueId().toString());
            setActive(!kForeign.isEmpty());
        }
    }

    private static void setActive(boolean active) {
        if (active != kActive) {
            kActive = active;
            MiscNatives.permissionProvider(active);
        }
    }

    public static void declare(Permission permission) {
        Map<String, Boolean> children = permission.getChildren();
        String[] nodes = children.keySet().toArray(new String[0]);
        boolean[] values = new boolean[nodes.length];
        for (int at = 0; at < nodes.length; at++) {
            values[at] = children.get(nodes[at]);
        }
        MiscNatives.permissionDeclare(permission.getName().toLowerCase(java.util.Locale.ROOT),
            kindOf(permission.getDefault()), nodes, values);
    }

    public static void undeclare(String name) {
        MiscNatives.permissionUndeclare(name.toLowerCase(java.util.Locale.ROOT));
    }

    private static int kindOf(PermissionDefault value) {
        return switch (value) {
            case FALSE -> 0;
            case TRUE -> 1;
            case OP -> 2;
            case NOT_OP -> 3;
        };
    }
}
