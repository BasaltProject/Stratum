// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.server.network;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import org.basaltmc.stratum.bukkit.Unsupported;
import com.mojang.authlib.GameProfile;
import java.net.SocketAddress;
import java.util.Set;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.protocol.common.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.GameProtocols;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.network.protocol.game.ServerboundBlockEntityTagQueryPacket;
import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundChangeGameModePacket;
import net.minecraft.network.protocol.game.ServerboundChatAckPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandSignedPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundChatSessionUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundChunkBatchReceivedPacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundClientTickEndPacket;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;
import net.minecraft.network.protocol.game.ServerboundConfigurationAcknowledgedPacket;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundContainerSlotStateChangedPacket;
import net.minecraft.network.protocol.game.ServerboundDebugSubscriptionRequestPacket;
import net.minecraft.network.protocol.game.ServerboundEditBookPacket;
import net.minecraft.network.protocol.game.ServerboundEntityTagQueryPacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundJigsawGeneratePacket;
import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundPaddleBoatPacket;
import net.minecraft.network.protocol.game.ServerboundPickItemFromBlockPacket;
import net.minecraft.network.protocol.game.ServerboundPickItemFromEntityPacket;
import net.minecraft.network.protocol.game.ServerboundPlaceRecipePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerLoadedPacket;
import net.minecraft.network.protocol.game.ServerboundPunchPacket;
import net.minecraft.network.protocol.game.ServerboundRecipeBookChangeSettingsPacket;
import net.minecraft.network.protocol.game.ServerboundRecipeBookSeenRecipePacket;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import net.minecraft.network.protocol.game.ServerboundSelectBundleItemPacket;
import net.minecraft.network.protocol.game.ServerboundSelectTradePacket;
import net.minecraft.network.protocol.game.ServerboundSetBeaconPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSetCommandBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSetCommandMinecartPacket;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.network.protocol.game.ServerboundSetGameRulePacket;
import net.minecraft.network.protocol.game.ServerboundSetJigsawBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSetStructureBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSetTestBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundSpectatorActionPacket;
import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;
import net.minecraft.network.protocol.game.ServerboundTestInstanceBlockActionPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.network.protocol.ping.ServerboundPingRequestPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;

public class ServerGamePacketListenerImpl extends ServerCommonPacketListenerImpl implements ServerGamePacketListener, ServerPlayerConnection, TickablePacketListener, GameProtocols.Context {
    public ServerGamePacketListenerImpl(Connection connection) {
        super(connection);
    }

    public static final int CLIENT_LOADED_TIMEOUT_TIME = 60;
    public ServerPlayer player;
    public final PlayerChunkSender chunkSender = null;
    public ServerGamePacketListenerImpl(MinecraftServer server, Connection connection, ServerPlayer player, CommonListenerCookie cookie) {
        super(server, connection, cookie);
        this.player = player;
        if (player != null) {
            player.connection = this;
        }
    }
    public void tick() {

    }
    public void resetFlyingTicks() {
    }
    public void resetPosition() {

    }
    public boolean isAcceptingMessages() {
        return connection.isConnected();
    }
    public boolean shouldHandleMessage(Packet<?> packet) {
        return isAcceptingMessages();
    }
    public void disconnect(DisconnectionDetails details) {
        org.bukkit.entity.Player bukkit = player == null ? null : player.bukkitPlayer();
        if (bukkit != null && bukkit.isOnline()) {
            bukkit.kick(details.reason().asAdventure());
        } else {
            super.disconnect(details);
        }
    }
    public void disconnect(Component reason) {
        disconnect(new DisconnectionDetails(reason));
    }
    protected GameProfile playerProfile() {
        return new GameProfile((player.bukkitPlayer()).getUniqueId(), (player.bukkitPlayer()).getName());
    }
    public void handlePlayerInput(ServerboundPlayerInputPacket packet) {
        forwardToNative(packet);
    }
    public void handleMoveVehicle(ServerboundMoveVehiclePacket packet) {
        forwardToNative(packet);
    }
    public void handleAcceptTeleportPacket(ServerboundAcceptTeleportationPacket packet) {
        forwardToNative(packet);
    }
    public void handleAcceptPlayerLoad(ServerboundPlayerLoadedPacket packet) {
        forwardToNative(packet);
    }
    public void handleRecipeBookSeenRecipePacket(ServerboundRecipeBookSeenRecipePacket packet) {
        forwardToNative(packet);
    }
    public void handleBundleItemSelectedPacket(ServerboundSelectBundleItemPacket packet) {
        forwardToNative(packet);
    }
    public void handleRecipeBookChangeSettingsPacket(ServerboundRecipeBookChangeSettingsPacket packet) {
        forwardToNative(packet);
    }
    public void handleSeenAdvancements(ServerboundSeenAdvancementsPacket packet) {
        forwardToNative(packet);
    }
    public void handleCustomCommandSuggestions(ServerboundCommandSuggestionPacket packet) {
        forwardToNative(packet);
    }
    public void handleSetCommandBlock(ServerboundSetCommandBlockPacket packet) {
        forwardToNative(packet);
    }
    public void handleSetCommandMinecart(ServerboundSetCommandMinecartPacket packet) {
        forwardToNative(packet);
    }
    public void handlePickItemFromBlock(ServerboundPickItemFromBlockPacket packet) {
        forwardToNative(packet);
    }
    public void handlePickItemFromEntity(ServerboundPickItemFromEntityPacket packet) {
        forwardToNative(packet);
    }
    public void handleRenameItem(ServerboundRenameItemPacket packet) {
        forwardToNative(packet);
    }
    public void handleSetBeaconPacket(ServerboundSetBeaconPacket packet) {
        forwardToNative(packet);
    }
    public void handleSetGameRule(ServerboundSetGameRulePacket packet) {
        forwardToNative(packet);
    }
    public void handleSetStructureBlock(ServerboundSetStructureBlockPacket packet) {
        forwardToNative(packet);
    }
    public void handleSetTestBlock(ServerboundSetTestBlockPacket packet) {
        forwardToNative(packet);
    }
    public void handleTestInstanceBlockAction(ServerboundTestInstanceBlockActionPacket packet) {
        forwardToNative(packet);
    }
    public void handleSetJigsawBlock(ServerboundSetJigsawBlockPacket packet) {
        forwardToNative(packet);
    }
    public void handleJigsawGenerate(ServerboundJigsawGeneratePacket packet) {
        forwardToNative(packet);
    }
    public void handleSelectTrade(ServerboundSelectTradePacket packet) {
        forwardToNative(packet);
    }
    public void handleEditBook(ServerboundEditBookPacket packet) {
        forwardToNative(packet);
    }
    public void handleEntityTagQuery(ServerboundEntityTagQueryPacket packet) {
        forwardToNative(packet);
    }
    public void handleContainerSlotStateChanged(ServerboundContainerSlotStateChangedPacket packet) {
        forwardToNative(packet);
    }
    public void handleBlockEntityTagQuery(ServerboundBlockEntityTagQueryPacket packet) {
        forwardToNative(packet);
    }
    public void handleMovePlayer(ServerboundMovePlayerPacket packet) {
        forwardToNative(packet);
    }
    public void teleport(double x, double y, double z, float yRot, float xRot) {
        org.bukkit.entity.Player bukkit = player.bukkitPlayer();
        bukkit.teleport(new org.bukkit.Location(bukkit.getWorld(), x, y, z, yRot, xRot));
    }
    public void teleport(PositionMoveRotation destination, Set<Relative> relatives) {
        org.bukkit.entity.Player bukkit = player.bukkitPlayer();
        org.bukkit.Location at = bukkit.getLocation();
        net.minecraft.world.phys.Vec3 position = destination.position();
        double x = relatives.contains(Relative.X) ? at.getX() + position.x : position.x;
        double y = relatives.contains(Relative.Y) ? at.getY() + position.y : position.y;
        double z = relatives.contains(Relative.Z) ? at.getZ() + position.z : position.z;
        float yRot = relatives.contains(Relative.Y_ROT) ? at.getYaw() + destination.yRot() : destination.yRot();
        float xRot = relatives.contains(Relative.X_ROT) ? at.getPitch() + destination.xRot() : destination.xRot();
        teleport(x, y, z, yRot, xRot);
    }
    public void handlePlayerAction(ServerboundPlayerActionPacket packet) {
        forwardToNative(packet);
    }
    public void handleUseItemOn(ServerboundUseItemOnPacket packet) {
        forwardToNative(packet);
    }
    public void handleUseItem(ServerboundUseItemPacket packet) {
        forwardToNative(packet);
    }
    public void handleTeleportToEntityPacket(ServerboundTeleportToEntityPacket packet) {
        forwardToNative(packet);
    }
    public void handlePaddleBoat(ServerboundPaddleBoatPacket packet) {
        forwardToNative(packet);
    }
    public void onDisconnect(DisconnectionDetails details) {
        if (connection.isConnected()) {
            connection.disconnect(details);
        }
    }
    public void ackBlockChangesUpTo(int packetSequenceNr) {
        send(new net.minecraft.network.protocol.game.ClientboundBlockChangedAckPacket(packetSequenceNr));
    }
    public void handleSetCarriedItem(ServerboundSetCarriedItemPacket packet) {
        forwardToNative(packet);
    }
    public void handleChat(ServerboundChatPacket packet) {
        forwardToNative(packet);
    }
    public void handleChatCommand(ServerboundChatCommandPacket packet) {
        forwardToNative(packet);
    }
    public void handleSignedChatCommand(ServerboundChatCommandSignedPacket packet) {
        forwardToNative(packet);
    }
    public void handleChatAck(ServerboundChatAckPacket packet) {
        forwardToNative(packet);
    }
    public void handlePunch(ServerboundPunchPacket packet) {
        forwardToNative(packet);
    }
    public void handlePlayerCommand(ServerboundPlayerCommandPacket packet) {
        forwardToNative(packet);
    }
    public void sendPlayerChatMessage(PlayerChatMessage message, ChatType.Bound chatType) {
        send(new net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket(message.unsignedContent() != null ? message.unsignedContent() : Component.literal(message.signedContent()), chatType));
    }
    public void sendDisguisedChatMessage(Component content, ChatType.Bound chatType) {
        send(new net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket(content, chatType));
    }
    public SocketAddress getRemoteAddress() {
        return connection.getRemoteAddress();
    }
    public void switchToConfig() {
        send(net.minecraft.network.protocol.game.ClientboundStartConfigurationPacket.INSTANCE);
    }
    public void handlePingRequest(ServerboundPingRequestPacket packet) {
        forwardToNative(packet);
    }
    public void handleAttack(ServerboundAttackPacket packet) {
        forwardToNative(packet);
    }
    public void handleInteract(ServerboundInteractPacket packet) {
        forwardToNative(packet);
    }
    public void handleSpectatorAction(ServerboundSpectatorActionPacket packet) {
        forwardToNative(packet);
    }
    public void handleClientCommand(ServerboundClientCommandPacket packet) {
        forwardToNative(packet);
    }
    public void handleContainerClose(ServerboundContainerClosePacket packet) {
        forwardToNative(packet);
    }
    public void handleContainerClick(ServerboundContainerClickPacket packet) {
        forwardToNative(packet);
    }
    public void handlePlaceRecipe(ServerboundPlaceRecipePacket packet) {
        forwardToNative(packet);
    }
    public void handleContainerButtonClick(ServerboundContainerButtonClickPacket packet) {
        forwardToNative(packet);
    }
    public void handleSetCreativeModeSlot(ServerboundSetCreativeModeSlotPacket packet) {
        forwardToNative(packet);
    }
    public void handleSignUpdate(ServerboundSignUpdatePacket packet) {
        forwardToNative(packet);
    }
    public void handlePlayerAbilities(ServerboundPlayerAbilitiesPacket packet) {
        forwardToNative(packet);
    }
    public void handleClientInformation(ServerboundClientInformationPacket packet) {
        forwardToNative(packet);
    }
    public void handleChangeDifficulty(ServerboundChangeDifficultyPacket packet) {
        forwardToNative(packet);
    }
    public void handleChangeGameMode(ServerboundChangeGameModePacket packet) {
        forwardToNative(packet);
    }
    public void handleLockDifficulty(ServerboundLockDifficultyPacket packet) {
        forwardToNative(packet);
    }
    public void handleChatSessionUpdate(ServerboundChatSessionUpdatePacket packet) {
        forwardToNative(packet);
    }
    public void handleConfigurationAcknowledged(ServerboundConfigurationAcknowledgedPacket packet) {
        forwardToNative(packet);
    }
    public void handleChunkBatchReceived(ServerboundChunkBatchReceivedPacket packet) {
        forwardToNative(packet);
    }
    public void handleDebugSubscriptionRequest(ServerboundDebugSubscriptionRequestPacket packet) {
        forwardToNative(packet);
    }
    public void handleCustomPayload(ServerboundCustomPayloadPacket packet) {
        forwardToNative(packet);
    }
    public void handleClientTickEnd(ServerboundClientTickEndPacket packet) {
        forwardToNative(packet);
    }
    public boolean hasInfiniteMaterials() {
        return (player.bukkitPlayer()).getGameMode() == org.bukkit.GameMode.CREATIVE;
    }
    public boolean canUseCommandBlocks() {
        return (player.bukkitPlayer()).isOp() && (player.bukkitPlayer()).getGameMode() == org.bukkit.GameMode.CREATIVE;
    }
    @Override
    protected void forwardToNative(Packet<?> packet) {
        org.basaltmc.stratum.bukkit.net.WirePacket wire = org.basaltmc.stratum.bukkit.net.PacketEncoding.toWire(packet);
        io.netty.buffer.ByteBuf body = io.netty.buffer.Unpooled.buffer();
        try {
            wire.write(body);
            byte[] bytes = new byte[body.readableBytes()];
            body.readBytes(bytes);
            org.basaltmc.stratum.bukkit.Natives.playerPacketReceive((player.bukkitPlayer()).getUniqueId().toString(), wire.packetId(), bytes);
        } finally {
            body.release();
        }
    }
    @Override
    public int latency() {
        return (player.bukkitPlayer()).getPing();
    }
    public ServerPlayer getPlayer() {
        return player;
    }
    public boolean hasClientLoaded() {
        return true;
    }
    public void tickClientLoadTimeout() {
    }
    public void markClientUnloadedAfterDeath() {
    }
}
