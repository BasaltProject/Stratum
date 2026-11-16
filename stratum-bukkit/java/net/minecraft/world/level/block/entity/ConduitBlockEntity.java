// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.basaltmc.stratum.bukkit.facade.TileNbt;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ConduitBlockEntity extends BlockEntity {
    public ConduitBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityType.named("conduit"), worldPosition, blockState);
    }

    public ConduitBlockEntity(ServerLevel level, BlockPos pos, CompoundTag nbt) {
        super(level, pos, nbt);
    }

    public boolean isActive() {
        org.bukkit.block.Conduit conduit = bukkit(org.bukkit.block.Conduit.class);
        return conduit != null && conduit.isActive();
    }

    public int getRange() {
        org.bukkit.block.Conduit conduit = bukkit(org.bukkit.block.Conduit.class);
        return conduit == null ? 0 : conduit.getRange();
    }

    public boolean hasTarget() {
        org.bukkit.block.Conduit conduit = bukkit(org.bukkit.block.Conduit.class);
        return conduit != null && conduit.hasTarget();
    }
    public int tickCount;
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
    }
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
    }
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return super.getUpdateTag(registries);
    }
    public static void clientTick(Level level, BlockPos pos, BlockState state, ConduitBlockEntity entity) {
    }
    public static void serverTick(Level level, BlockPos pos, BlockState state, ConduitBlockEntity entity) {
    }
    public boolean isHunting() {
        return false;
    }
    public float getActiveRotation(float a) {
        return (float) 0;
    }
}
