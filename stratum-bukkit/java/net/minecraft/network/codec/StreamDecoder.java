// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.network.codec;

public interface StreamDecoder<I, T> {
    T decode(I input);
}
