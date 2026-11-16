// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CampfireBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty SIGNAL_FIRE = BlockStateProperties.SIGNAL_FIRE;
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected CampfireBlock(int index) {
        super(index);
    }

    public static boolean canLight(BlockState state) {
        return state.is(net.minecraft.tags.BlockTags.CAMPFIRES) && state.hasProperty(BlockStateProperties.WATERLOGGED)
            && !state.getValue(BlockStateProperties.WATERLOGGED) && state.hasProperty(BlockStateProperties.LIT)
            && !state.getValue(BlockStateProperties.LIT);
    }

    public static boolean isLitCampfire(BlockState state) {
        return state.hasProperty(BlockStateProperties.LIT) && state.is(net.minecraft.tags.BlockTags.CAMPFIRES) && state.getValue(BlockStateProperties.LIT);
    }

    public static boolean isSmokeyPos(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos) {
        net.minecraft.world.phys.AABB post = new net.minecraft.world.phys.AABB(0.375, 0.0, 0.375, 0.625, 1.0, 0.625);
        for (int i = 1; i <= 5; i++) {
            net.minecraft.core.BlockPos checked = pos.below(i);
            BlockState state = level.getBlockState(checked);
            if (isLitCampfire(state)) {
                return true;
            }
            boolean blocked = state.getCollisionShape(level, pos, net.minecraft.world.phys.shapes.CollisionContext.empty()).toAabbs()
                .stream().anyMatch(box -> box.intersects(post));
            if (blocked) {
                return isLitCampfire(level.getBlockState(checked.below()));
            }
        }
        return false;
    }
    public CampfireBlock(boolean spawnParticles, int fireDamage, BlockBehaviour.Properties properties) {
        super((int) 0);
    }
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
    }
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        super.entityInside(state, level, pos, entity, effectApplier, isPrecise);
    }
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context);
    }
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
    }
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return super.getShape(state, level, pos, context);
    }
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
    }
    public static void douse(Entity source, LevelAccessor level, BlockPos pos, BlockState state) {
    }
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        return SimpleWaterloggedBlock.super.placeLiquid(level, pos, state, fluidState);
    }
    protected void onProjectileHit(Level level, BlockState state, BlockHitResult blockHit, Projectile projectile) {
        super.onProjectileHit(level, state, blockHit, projectile);
    }
    public static void makeParticles(Level level, BlockPos pos, boolean isSignalFire, boolean smoking) {
    }
    protected FluidState getFluidState(BlockState state) {
        return super.getFluidState(state);
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
    public BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return super.newBlockEntity(worldPosition, blockState);
    }
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
        return super.getTicker(level, blockState, type);
    }
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return super.isPathfindable(state, type);
    }
}
