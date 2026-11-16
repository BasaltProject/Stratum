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
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.util.function.Supplier;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stat;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ChestBlock extends AbstractChestBlock<ChestBlockEntity> implements SimpleWaterloggedBlock {
    public static final int EVENT_SET_OPEN_COUNT = 1;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<ChestType> TYPE = BlockStateProperties.CHEST_TYPE;
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

    public static BlockPos getConnectedBlockPos(BlockPos pos, BlockState state) {
        Direction facing = state.getValue(FACING);
        return pos.relative(state.getValue(TYPE) == ChestType.LEFT ? facing.getClockWise() : facing.getCounterClockWise());
    }

    public static DoubleBlockCombiner.BlockType getBlockType(BlockState state) {
        ChestType type = state.getValue(TYPE);
        if (type == ChestType.SINGLE) {
            return DoubleBlockCombiner.BlockType.SINGLE;
        }
        return type == ChestType.RIGHT ? DoubleBlockCombiner.BlockType.FIRST : DoubleBlockCombiner.BlockType.SECOND;
    }

    public boolean chestCanConnectTo(BlockState blockState) {
        return blockState.is(this);
    }

    protected ChestType getChestType(net.minecraft.world.level.Level level, BlockPos pos, Direction facingDirection) {
        if (facingDirection == candidatePartnerFacing(level, pos, facingDirection.getClockWise())) {
            return ChestType.LEFT;
        }
        return facingDirection == candidatePartnerFacing(level, pos, facingDirection.getCounterClockWise()) ? ChestType.RIGHT : ChestType.SINGLE;
    }

    private Direction candidatePartnerFacing(net.minecraft.world.level.Level level, BlockPos pos, Direction neighbourDirection) {
        BlockState state = level.getBlockState(pos.relative(neighbourDirection));
        return chestCanConnectTo(state) && state.getValue(TYPE) == ChestType.SINGLE ? state.getValue(FACING) : null;
    }

    public net.minecraft.world.level.block.entity.BlockEntityType<? extends ChestBlockEntity> blockEntityType() {
        return net.minecraft.world.level.block.entity.BlockEntityTypes.CHEST;
    }

    public DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> combine(BlockState state,
            net.minecraft.world.level.Level level, BlockPos pos, boolean ignoreBeingBlocked) {
        java.util.function.BiPredicate<net.minecraft.world.level.LevelAccessor, BlockPos> blocked =
            ignoreBeingBlocked ? (accessor, blockPos) -> false : ChestBlock::isChestBlockedAt;
        return DoubleBlockCombiner.combineWithNeigbour(blockEntityType(), ChestBlock::getBlockType,
            ChestBlock::getConnectedDirection, FACING, state, level, pos, blocked);
    }

    public static boolean isChestBlockedAt(net.minecraft.world.level.LevelAccessor level, BlockPos pos) {
        return isBlockedChestByBlock(level, pos) || isCatSittingOnChest(level, pos);
    }

    private static boolean isBlockedChestByBlock(net.minecraft.world.level.BlockGetter level, BlockPos pos) {
        BlockPos above = pos.above();
        return level.getBlockState(above).isRedstoneConductor(level, above);
    }

    private static boolean isCatSittingOnChest(net.minecraft.world.level.LevelAccessor level, BlockPos pos) {
        net.minecraft.world.phys.AABB above = new net.minecraft.world.phys.AABB(pos.getX(), pos.getY() + 1, pos.getZ(),
            pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1);
        for (net.minecraft.world.entity.animal.feline.Cat cat : level.getEntitiesOfClass(net.minecraft.world.entity.animal.feline.Cat.class, above)) {
            if (cat.isInSittingPose()) {
                return true;
            }
        }
        return false;
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
    protected ChestBlock(Supplier<BlockEntityType<? extends ChestBlockEntity>> blockEntityType, SoundEvent openSound, SoundEvent closeSound, BlockBehaviour.Properties properties) {
        super((int) 0);
    }
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
    }
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return super.getShape(state, level, pos, context);
    }
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context);
    }
    protected FluidState getFluidState(BlockState state) {
        return super.getFluidState(state);
    }
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }
    protected Stat<Identifier> getOpenChestStat() {
        throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.level.block.ChestBlock#getOpenChestStat");
    }
    protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return super.getMenuProvider(state, level, pos);
    }
    public static DoubleBlockCombiner.Combiner<ChestBlockEntity, Float2FloatFunction> opennessCombiner(LidBlockEntity entity) {
        throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.level.block.ChestBlock#opennessCombiner");
    }
    public BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return super.newBlockEntity(worldPosition, blockState);
    }
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
        return super.getTicker(level, blockState, type);
    }
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return super.hasAnalogOutputSignal(state);
    }
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        return super.getAnalogOutputSignal(state, level, pos, direction);
    }
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return super.rotate(state, rotation);
    }
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return super.mirror(state, mirror);
    }
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return super.isPathfindable(state, type);
    }
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
    }
    public SoundEvent getOpenChestSound() {
        throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.level.block.ChestBlock#getOpenChestSound");
    }
    public SoundEvent getCloseChestSound() {
        throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.level.block.ChestBlock#getCloseChestSound");
    }
}
