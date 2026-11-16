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
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class JukeboxBlock extends BaseEntityBlock {
    public static final net.minecraft.world.level.block.state.properties.BooleanProperty HAS_RECORD =
        net.minecraft.world.level.block.state.properties.BlockStateProperties.HAS_RECORD;

    protected JukeboxBlock(int index) {
        super(index);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hitResult) {
        if (level instanceof ServerLevel server && player.getBukkitEntity() instanceof org.bukkit.entity.Player viewer) {
            if (server.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()).getState() instanceof org.bukkit.block.Jukebox jukebox
                && jukebox.hasRecord()) {
                jukebox.eject();
                return InteractionResult.SUCCESS;
            }
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hitResult) {
        if (level instanceof ServerLevel server
            && server.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()).getState() instanceof org.bukkit.block.Jukebox jukebox
            && !jukebox.hasRecord()) {
            org.bukkit.inventory.ItemStack disc = org.bukkit.craftbukkit.inventory.CraftItemStack.asCraftMirror(stack);
            if (disc.getType().isRecord()) {
                jukebox.setRecord(disc.asOne());
                jukebox.update();
                if (!(player.getBukkitEntity() instanceof org.bukkit.entity.Player p && p.getGameMode() == org.bukkit.GameMode.CREATIVE)) {
                    stack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
    protected JukeboxBlock(BlockBehaviour.Properties properties) {
        super((int) 0);
    }
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity by, ItemStack itemStack) {
        super.setPlacedBy(level, pos, state, by, itemStack);
    }
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }
    public BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return super.newBlockEntity(worldPosition, blockState);
    }
    public boolean isSignalSource(BlockState state) {
        return super.isSignalSource(state);
    }
    protected int ownSignal(BlockState state, BlockGetter level, BlockPos pos) {
        return super.ownSignal(state, level, pos);
    }
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return super.hasAnalogOutputSignal(state);
    }
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        return super.getAnalogOutputSignal(state, level, pos, direction);
    }
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
        return super.getTicker(level, blockState, type);
    }
}
