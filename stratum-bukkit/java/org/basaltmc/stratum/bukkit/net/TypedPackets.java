package org.basaltmc.stratum.bukkit.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.GameProtocols;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import org.basaltmc.stratum.bukkit.facade.ServerAccess;

/** Decodes play packets into facade objects for pipelines whose handlers expect typed packets. */
final class TypedPackets {
    private static final int kMaxTracked = 4096;
    private static final Map<Packet<?>, byte[][]> tracked = Collections.synchronizedMap(new IdentityHashMap<>());
    private static ProtocolInfo<ClientGamePacketListener> clientboundInfo;
    private static ProtocolInfo<ServerGamePacketListener> serverboundInfo;

    private TypedPackets() {}

    private static RegistryFriendlyByteBuf wrap(ByteBuf buffer) {
        return new RegistryFriendlyByteBuf(buffer, ServerAccess.registries());
    }

    static synchronized ProtocolInfo<ClientGamePacketListener> clientboundInfo() {
        if (clientboundInfo == null) {
            Function<ByteBuf, RegistryFriendlyByteBuf> wrapper = TypedPackets::wrap;
            clientboundInfo = GameProtocols.CLIENTBOUND_TEMPLATE.bind(wrapper);
        }
        return clientboundInfo;
    }

    static synchronized ProtocolInfo<ServerGamePacketListener> serverboundInfo() {
        if (serverboundInfo == null) {
            Function<ByteBuf, RegistryFriendlyByteBuf> wrapper = TypedPackets::wrap;
            serverboundInfo = GameProtocols.SERVERBOUND_TEMPLATE.bind(wrapper, new GameProtocols.Context() {
                public boolean hasInfiniteMaterials() {
                    return true;
                }

                public boolean canUseCommandBlocks() {
                    return true;
                }
            });
        }
        return serverboundInfo;
    }

    private static StreamCodec<ByteBuf, Packet<? super ClientGamePacketListener>> clientbound() {
        return clientboundInfo().codec();
    }

    private static StreamCodec<ByteBuf, Packet<? super ServerGamePacketListener>> serverbound() {
        return serverboundInfo().codec();
    }

    private static byte[] frame(int id, byte[] body) {
        ByteBuf out = Unpooled.buffer(body.length + 5);
        ChannelBridge.writeVarInt(out, id);
        out.writeBytes(body);
        byte[] bytes = new byte[out.readableBytes()];
        out.readBytes(bytes);
        out.release();
        return bytes;
    }

    private static byte[] encode(Packet<?> packet) {
        WirePacket wire = PacketEncoding.toWire(packet);
        ByteBuf out = Unpooled.buffer();
        try {
            ChannelBridge.writeVarInt(out, wire.packetId());
            wire.write(out);
            byte[] bytes = new byte[out.readableBytes()];
            out.readBytes(bytes);
            return bytes;
        } finally {
            out.release();
        }
    }

    /** The typed form of a native packet body, or null when the facade cannot represent it. */
    static Packet<?> decode(boolean toServer, int id, byte[] body) {
        byte[] whole = frame(id, body);
        ByteBuf in = Unpooled.wrappedBuffer(whole);
        try {
            Packet<?> packet = toServer ? serverbound().decode(in) : clientbound().decode(in);
            byte[] baseline = encode(packet);
            if (tracked.size() > kMaxTracked) {
                tracked.clear();
            }
            tracked.put(packet, new byte[][] {whole, baseline});
            return packet;
        } catch (RuntimeException | LinkageError unsupported) {
            return null;
        } finally {
            in.release();
        }
    }

    /** The frame to send for a packet: the original bytes while no listener changed it, else its fresh encoding. */
    static byte[] frameOf(Packet<?> packet) {
        byte[] fresh = encode(packet);
        byte[][] known = tracked.remove(packet);
        return known != null && java.util.Arrays.equals(known[1], fresh) ? known[0] : fresh;
    }
}
