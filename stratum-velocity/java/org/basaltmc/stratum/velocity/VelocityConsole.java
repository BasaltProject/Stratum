// Independent reimplementation of the Velocity API shape over Basalt natives. See stratum-velocity/NOTICE.md
package org.basaltmc.stratum.velocity;

import com.velocitypowered.api.permission.PermissionFunction;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

final class VelocityConsole implements ConsoleCommandSource {
    @Override
    public void sendMessage(Component message) {
        Natives.log(PlainTextComponentSerializer.plainText().serialize(message));
    }

    private volatile PermissionFunction permissions;

    void permissions(PermissionFunction function) {
        permissions = function;
    }

    /** The function a permissions plugin set up for the console, or null. */
    PermissionFunction pluginPermissions() {
        return permissions;
    }

    @Override
    public Tristate getPermissionValue(String permission) {
        return BasaltVelocity.chainValue("", permission);
    }
}
