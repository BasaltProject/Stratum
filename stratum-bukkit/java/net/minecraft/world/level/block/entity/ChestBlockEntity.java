// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ChestBlockEntity extends RandomizableContainerBlockEntity implements LidBlockEntity {
    protected ChestBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
    }

    public ChestBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityType.named("chest"), worldPosition, blockState);
    }

    public ChestBlockEntity(ServerLevel level, BlockPos pos, CompoundTag nbt) {
        super(level, pos, nbt);
    }

    @Override
    public int getContainerSize() {
        return 27;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.chest");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return net.minecraft.world.inventory.ChestMenu.threeRows(containerId, inventory, this);
    }

    @Override
    public float getOpenNess(float a) {
        return org.basaltmc.stratum.bukkit.facade.LidControl.openness(this);
    }
    public static int getOpenCount(net.minecraft.world.level.BlockGetter level, BlockPos pos) {
        if (level instanceof net.minecraft.world.level.Level real
                && real.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()).getState() instanceof org.bukkit.block.Chest chest) {
            return chest.getInventory().getViewers().size();
        }
        return 0;
    }

    @Override
    public void startOpen(net.minecraft.world.entity.ContainerUser user) {
        org.basaltmc.stratum.bukkit.facade.LidControl.set(this, true);
    }

    @Override
    public void stopOpen(net.minecraft.world.entity.ContainerUser user) {
        org.basaltmc.stratum.bukkit.facade.LidControl.set(this, false);
    }
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
    }
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
    }
    public static void lidAnimateTick(Level level, BlockPos pos, BlockState state, ChestBlockEntity entity) {
    }
    public boolean triggerEvent(int b0, int b1) {
        return super.triggerEvent(b0, b1);
    }
    public List<ContainerUser> getEntitiesWithContainerOpen() {
        return super.getEntitiesWithContainerOpen();
    }
    protected NonNullList<ItemStack> getItems() {
        return super.getItems();
    }
    protected void setItems(NonNullList<ItemStack> items) {
        super.setItems(items);
    }
    public static void swapContents(ChestBlockEntity one, ChestBlockEntity two) {
    }
    public void recheckOpen() {
    }
    protected void signalOpenCount(Level level, BlockPos pos, BlockState blockState, int previous, int current) {
    }
}
