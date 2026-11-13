// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.network.protocol.login.custom;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;

public record DiscardedQueryPayload(Identifier id) implements CustomQueryPayload {
    public void write(FriendlyByteBuf output) {}
}
