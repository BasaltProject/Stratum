// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class DropperBlock extends DispenserBlock {
    protected DropperBlock(int index) {
        super(index);
    }
    public DropperBlock(BlockBehaviour.Properties properties) {
        super((int) 0);
    }
    protected DispenseItemBehavior getDispenseMethod(Level level, ItemStack itemStack) {
        return super.getDispenseMethod(level, itemStack);
    }
    public BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return super.newBlockEntity(worldPosition, blockState);
    }
    protected void dispenseFrom(ServerLevel level, BlockState state, BlockPos pos) {
        super.dispenseFrom(level, state, pos);
    }
}
