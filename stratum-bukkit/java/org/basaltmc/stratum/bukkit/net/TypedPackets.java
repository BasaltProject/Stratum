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

/** Decodes packets of every protocol phase into facade objects for pipelines whose handlers expect typed packets. */
final class TypedPackets {
    private static final int kMaxTracked = 4096;
    private static final Map<Packet<?>, byte[][]> tracked = Collections.synchronizedMap(new IdentityHashMap<>());
    private static final boolean kDebug = System.getenv("BASALT_TYPED_DEBUG") != null;
    private static final java.util.Set<String> failed = java.util.concurrent.ConcurrentHashMap.newKeySet();
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

    private static final int kPlay = 4;

    static ProtocolInfo<?> infoOf(boolean toServer, int phase) {
        return switch (phase) {
            case 0 -> toServer ? net.minecraft.network.protocol.handshake.HandshakeProtocols.SERVERBOUND : null;
            case 1 -> toServer ? net.minecraft.network.protocol.status.StatusProtocols.SERVERBOUND
                : net.minecraft.network.protocol.status.StatusProtocols.CLIENTBOUND;
            case 2 -> toServer ? net.minecraft.network.protocol.login.LoginProtocols.SERVERBOUND
                : net.minecraft.network.protocol.login.LoginProtocols.CLIENTBOUND;
            case 3 -> toServer ? net.minecraft.network.protocol.configuration.ConfigurationProtocols.SERVERBOUND
                : net.minecraft.network.protocol.configuration.ConfigurationProtocols.CLIENTBOUND;
            default -> toServer ? serverboundInfo() : clientboundInfo();
        };
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static StreamCodec<ByteBuf, Packet<?>> codecOf(boolean toServer, int phase) {
        ProtocolInfo<?> info = infoOf(toServer, phase);
        return info == null ? null : (StreamCodec) info.codec();
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

    /** The full frame (id and body) of a facade packet in the given protocol phase. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    static byte[] encode(Packet<?> packet, int phase) {
        ByteBuf out = Unpooled.buffer();
        try {
            if (phase == kPlay) {
                WirePacket wire = PacketEncoding.toWire(packet);
                ChannelBridge.writeVarInt(out, wire.packetId());
                wire.write(out);
            } else {
                StreamCodec codec = codecOf(packet.type().flow() == net.minecraft.network.protocol.PacketFlow.SERVERBOUND, phase);
                if (codec == null) {
                    throw new IllegalArgumentException("No protocol for " + packet.type().id() + " in phase " + phase);
                }
                codec.encode(out, packet);
            }
            byte[] bytes = new byte[out.readableBytes()];
            out.readBytes(bytes);
            return bytes;
        } finally {
            out.release();
        }
    }

    /** The typed form of a native packet body, or null when the facade cannot represent it. */
    static Packet<?> decode(boolean toServer, int phase, int id, byte[] body) {
        byte[] whole = frame(id, body);
        ByteBuf in = Unpooled.wrappedBuffer(whole);
        try {
            StreamCodec<ByteBuf, Packet<?>> codec = codecOf(toServer, phase);
            if (codec == null) {
                return null;
            }
            Packet<?> packet = codec.decode(in);
            byte[] baseline = encode(packet, phase);
            if (kDebug && !java.util.Arrays.equals(baseline, whole) && failed.add("m" + phase + (toServer ? "s" : "c") + id)) {
                System.err.println("typed round trip differs for phase " + phase + " id " + id + " " + packet.getClass().getSimpleName()
                    + " len " + whole.length + " vs " + baseline.length);
            }
            if (tracked.size() > kMaxTracked) {
                tracked.clear();
            }
            tracked.put(packet, new byte[][] {whole, baseline});
            return packet;
        } catch (RuntimeException | LinkageError unsupported) {
            if (kDebug && failed.add(phase + (toServer ? "s" : "c") + id)) {
                System.err.println("typed decode failed for phase " + phase + (toServer ? " serverbound " : " clientbound ") + id + ": " + unsupported);
                unsupported.printStackTrace();
            }
            return null;
        } finally {
            in.release();
        }
    }

    /** The frame to send for a packet: the original bytes while no listener changed it, else its fresh encoding. */
    static byte[] frameOf(Packet<?> packet, int phase) {
        byte[] fresh = encode(packet, phase);
        byte[][] known = tracked.remove(packet);
        return known != null && java.util.Arrays.equals(known[1], fresh) ? known[0] : fresh;
    }
}
