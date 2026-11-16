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
import net.minecraft.core.NonNullList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class DispenserBlockEntity extends RandomizableContainerBlockEntity {
    public static final int CONTAINER_SIZE = 9;
    protected DispenserBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
    }

    public DispenserBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityType.named("dispenser"), worldPosition, blockState);
    }

    public DispenserBlockEntity(ServerLevel level, BlockPos pos, CompoundTag nbt) {
        super(level, pos, nbt);
    }

    @Override
    public int getContainerSize() {
        return 9;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.dispenser");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new net.minecraft.world.inventory.DispenserMenu(containerId, inventory, this);
    }
    public int getRandomSlot(RandomSource random) {
        return (int) 0;
    }
    public ItemStack insertItem(ItemStack itemStack) {
        throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.level.block.entity.DispenserBlockEntity#insertItem");
    }
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
    }
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
    }
    protected NonNullList<ItemStack> getItems() {
        return super.getItems();
    }
    protected void setItems(NonNullList<ItemStack> items) {
        super.setItems(items);
    }
}
