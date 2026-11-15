// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.SculkSensorPhase;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;

public class SculkSensorBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final int ACTIVE_TICKS = 30;
    public static final int COOLDOWN_TICKS = 10;
    public static final EnumProperty<SculkSensorPhase> PHASE = BlockStateProperties.SCULK_SENSOR_PHASE;
    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static SculkSensorPhase getPhase(net.minecraft.world.level.block.state.BlockState state) {
        return state.getValue(PHASE);
    }

    public static boolean canActivate(net.minecraft.world.level.block.state.BlockState state) {
        return getPhase(state) == SculkSensorPhase.INACTIVE;
    }

    public static void deactivate(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos,
            net.minecraft.world.level.block.state.BlockState state) {
        level.setBlockAndUpdate(pos, state.setValue(PHASE, SculkSensorPhase.COOLDOWN).setValue(POWER, 0));
        level.scheduleTick(pos, state.getBlock(), COOLDOWN_TICKS);
        updateNeighbours(level, pos, state);
    }

    private static void updateNeighbours(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos,
            net.minecraft.world.level.block.state.BlockState state) {
        level.updateNeighborsAt(pos, state.getBlock());
        level.updateNeighborsAt(pos.below(), state.getBlock());
    }

    public int getActiveTicks() {
        return ACTIVE_TICKS;
    }

    protected SculkSensorBlock(int index) {
        super(index);
    }
}
