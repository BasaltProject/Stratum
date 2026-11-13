// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.network.chat;
import net.minecraft.server.level.ServerPlayer;
public interface ChatDecorator {
    ChatDecorator PLAIN = (player, plain) -> plain;
    Component decorate(ServerPlayer player, Component plain);
}
