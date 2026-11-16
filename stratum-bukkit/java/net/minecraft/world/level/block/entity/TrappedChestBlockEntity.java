// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;

public class TrappedChestBlockEntity extends ChestBlockEntity {
    public TrappedChestBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityType.named("trapped_chest"), worldPosition, blockState);
    }

    public TrappedChestBlockEntity(ServerLevel level, BlockPos pos, CompoundTag nbt) {
        super(level, pos, nbt);
    }
    protected void signalOpenCount(Level level, BlockPos pos, BlockState blockState, int previous, int current) {
        super.signalOpenCount(level, pos, blockState, previous, current);
    }
}
