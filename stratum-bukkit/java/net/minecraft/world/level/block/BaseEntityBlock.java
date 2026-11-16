// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public abstract class BaseEntityBlock extends Block implements EntityBlock {
    protected BaseEntityBlock(int index) {
        super(index);
    }

    @Override
    public net.minecraft.world.level.block.entity.BlockEntity newBlockEntity(net.minecraft.core.BlockPos pos,
            net.minecraft.world.level.block.state.BlockState state) {
        return org.basaltmc.stratum.bukkit.facade.BlockEntityTypes.detached(blockKey(), pos);
    }

    protected boolean hasInventory() {
        return false;
    }

    private static org.bukkit.block.Container containerAt(Level level, BlockPos pos) {
        if (level instanceof ServerLevel server
            && server.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()).getState() instanceof org.bukkit.block.Container container) {
            return container;
        }
        return null;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hitResult) {
        if (hasInventory() && player.getBukkitEntity() instanceof org.bukkit.entity.Player viewer) {
            org.bukkit.block.Container container = containerAt(level, pos);
            if (container != null) {
                viewer.openInventory(container.getInventory());
                return InteractionResult.SUCCESS;
            }
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return hasInventory();
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        org.bukkit.block.Container container = hasInventory() ? containerAt(level, pos) : null;
        if (container == null) {
            return 0;
        }
        org.bukkit.inventory.Inventory inventory = container.getInventory();
        float fill = 0.0F;
        for (org.bukkit.inventory.ItemStack stack : inventory.getContents()) {
            if (stack != null && !stack.getType().isAir()) {
                fill += (float) stack.getAmount() / Math.min(inventory.getMaxStackSize(), stack.getMaxStackSize());
            }
        }
        fill /= inventory.getSize();
        return (int) Math.floor(fill * 14.0F) + (fill > 0.0F ? 1 : 0);
    }
    protected BaseEntityBlock(BlockBehaviour.Properties properties) {
        super((int) 0);
    }
    protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int b0, int b1) {
        return super.triggerEvent(state, level, pos, b0, b1);
    }
    protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return super.getMenuProvider(state, level, pos);
    }
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> actual, BlockEntityType<E> expected, BlockEntityTicker<? super E> ticker) {
        throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.level.block.BaseEntityBlock#createTickerHelper");
    }
}
