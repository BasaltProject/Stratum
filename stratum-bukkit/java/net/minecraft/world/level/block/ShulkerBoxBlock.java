// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShulkerBoxBlock extends BaseEntityBlock {
    public static final net.minecraft.world.level.block.state.properties.EnumProperty<net.minecraft.core.Direction> FACING =
        net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;
    public static final net.minecraft.resources.Identifier CONTENTS = net.minecraft.resources.Identifier.withDefaultNamespace("contents");

    public net.minecraft.world.item.DyeColor getColor() {
        String path = blockKey().getPath();
        return path.equals("shulker_box") ? null
            : net.minecraft.world.item.DyeColor.byName(path.substring(0, path.length() - "_shulker_box".length()), null);
    }

    protected ShulkerBoxBlock(int index) {
        super(index);
    }

    @Override
    protected boolean hasInventory() {
        return true;
    }
    public static final Map<Direction, VoxelShape> SHAPES_OPEN_SUPPORT = null;
    public ShulkerBoxBlock(DyeColor color, BlockBehaviour.Properties properties) {
        super((int) 0);
    }
    public BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return super.newBlockEntity(worldPosition, blockState);
    }
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
        return super.getTicker(level, blockState, type);
    }
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context);
    }
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        return super.playerWillDestroy(level, pos, state, player);
    }
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return super.getDrops(state, params);
    }
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }
    protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
        return super.getBlockSupportShape(state, level, pos);
    }
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return super.getShape(state, level, pos, context);
    }
    protected boolean propagatesSkylightDown(BlockState state) {
        return super.propagatesSkylightDown(state);
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
}
