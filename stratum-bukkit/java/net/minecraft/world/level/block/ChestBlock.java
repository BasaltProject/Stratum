// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class ChestBlock extends AbstractChestBlock<ChestBlockEntity> implements SimpleWaterloggedBlock {
    public static final int EVENT_SET_OPEN_COUNT = 1;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<ChestType> TYPE = BlockStateProperties.CHEST_TYPE;
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

    public static BlockPos getConnectedBlockPos(BlockPos pos, BlockState state) {
        Direction facing = state.getValue(FACING);
        return pos.relative(state.getValue(TYPE) == ChestType.LEFT ? facing.getClockWise() : facing.getCounterClockWise());
    }

    protected ChestBlock(int index) {
        super(index);
    }

    public static Direction getConnectedDirection(BlockState state) {
        Direction facing = state.getValue(FACING);
        return state.getValue(TYPE) == ChestType.LEFT ? facing.getClockWise() : facing.getCounterClockWise();
    }

    public static net.minecraft.world.Container getContainer(ChestBlock chest, BlockState state, net.minecraft.world.level.Level level,
            BlockPos pos, boolean ignoreBeingBlocked) {
        if (!(level.getBlockEntity(pos) instanceof net.minecraft.world.level.block.entity.ChestBlockEntity own)) {
            return null;
        }
        ChestType type = state.getValue(TYPE);
        if (type == ChestType.SINGLE) {
            return own;
        }
        BlockState neighbour = level.getBlockState(getConnectedBlockPos(pos, state));
        if (!neighbour.is(state.getBlock()) || neighbour.getValue(TYPE) == ChestType.SINGLE || neighbour.getValue(TYPE) == type
            || neighbour.getValue(FACING) != state.getValue(FACING)) {
            return own;
        }
        if (level.getBlockEntity(getConnectedBlockPos(pos, state)) instanceof net.minecraft.world.level.block.entity.ChestBlockEntity other) {
            return type == ChestType.RIGHT ? new net.minecraft.world.CompoundContainer(own, other)
                : new net.minecraft.world.CompoundContainer(other, own);
        }
        return own;
    }
}
