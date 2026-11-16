// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import java.util.List;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EnchantingTableBlock extends BaseEntityBlock {
    public static final java.util.List<BlockPos> BOOKSHELF_OFFSETS = java.util.stream.IntStream.rangeClosed(-2, 2).boxed()
        .flatMap(x -> java.util.stream.IntStream.rangeClosed(-2, 2).boxed().flatMap(z -> java.util.stream.IntStream.rangeClosed(0, 1)
            .mapToObj(y -> new BlockPos(x, y, z))))
        .filter(offset -> Math.abs(offset.getX()) == 2 || Math.abs(offset.getZ()) == 2).toList();

    public static boolean isValidBookShelf(Level level, BlockPos pos, BlockPos offset) {
        return level.getBlockState(pos.offset(offset)).is(net.minecraft.tags.BlockTags.ENCHANTMENT_POWER_PROVIDER)
            && level.getBlockState(pos.offset(offset.getX() / 2, offset.getY(), offset.getZ() / 2))
                .is(net.minecraft.tags.BlockTags.ENCHANTMENT_POWER_TRANSMITTER);
    }

    protected EnchantingTableBlock(int index) {
        super(index);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hitResult) {
        if (level instanceof ServerLevel server && player.getBukkitEntity() instanceof org.bukkit.entity.Player viewer) {
            viewer.openEnchanting(new org.bukkit.Location(server.getWorld(), pos.getX(), pos.getY(), pos.getZ()), true);
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }
    protected EnchantingTableBlock(BlockBehaviour.Properties properties) {
        super((int) 0);
    }
    protected boolean useShapeForLightOcclusion(BlockState state) {
        return super.useShapeForLightOcclusion(state);
    }
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return super.getShape(state, level, pos, context);
    }
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
    }
    public BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return super.newBlockEntity(worldPosition, blockState);
    }
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
        return super.getTicker(level, blockState, type);
    }
    protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return super.getMenuProvider(state, level, pos);
    }
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return super.isPathfindable(state, type);
    }
}
