// Independent reimplementation of the Velocity API shape over Basalt natives. See stratum-velocity/NOTICE.md
package org.basaltmc.stratum.velocity;

import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

final class VelocityConsole implements ConsoleCommandSource {
    @Override
    public void sendMessage(Component message) {
        Natives.log(PlainTextComponentSerializer.plainText().serialize(message));
    }

    @Override
    public Tristate getPermissionValue(String permission) {
        return Tristate.TRUE;
    }
}
