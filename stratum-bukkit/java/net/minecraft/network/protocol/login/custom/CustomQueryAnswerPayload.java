// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.network.protocol.login.custom;
import net.minecraft.network.FriendlyByteBuf;
public interface CustomQueryAnswerPayload {
    void write(FriendlyByteBuf output);
}
