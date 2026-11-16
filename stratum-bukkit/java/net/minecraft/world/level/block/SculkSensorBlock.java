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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

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
    public SculkSensorBlock(BlockBehaviour.Properties properties) {
        super((int) 0);
    }
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context);
    }
    protected FluidState getFluidState(BlockState state) {
        return super.getFluidState(state);
    }
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
    }
    public void stepOn(Level level, BlockPos pos, BlockState onState, Entity entity) {
        super.stepOn(level, pos, onState, entity);
    }
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
    }
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
    }
    public BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return super.newBlockEntity(worldPosition, blockState);
    }
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
        return super.getTicker(level, blockState, type);
    }
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return super.getShape(state, level, pos, context);
    }
    protected boolean isSignalSource(BlockState state) {
        return super.isSignalSource(state);
    }
    protected int ownSignal(BlockState state, BlockGetter level, BlockPos pos) {
        return super.ownSignal(state, level, pos);
    }
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return super.getDirectSignal(state, level, pos, direction);
    }
    public void activate(Entity sourceEntity, Level level, BlockPos pos, BlockState state, int calculatedPower, int vibrationFrequency) {
    }
    public static void tryResonateVibration(Entity sourceEntity, Level level, BlockPos pos, int vibrationFrequency) {
    }
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
    }
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return super.hasAnalogOutputSignal(state);
    }
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        return super.getAnalogOutputSignal(state, level, pos, direction);
    }
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return super.isPathfindable(state, type);
    }
    protected boolean useShapeForLightOcclusion(BlockState state) {
        return super.useShapeForLightOcclusion(state);
    }
    protected void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack tool, boolean dropExperience) {
        super.spawnAfterBreak(state, level, pos, tool, dropExperience);
    }
}
