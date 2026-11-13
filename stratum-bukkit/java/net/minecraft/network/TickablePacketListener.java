// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.network;
public interface TickablePacketListener extends PacketListener {
    void tick();
}
