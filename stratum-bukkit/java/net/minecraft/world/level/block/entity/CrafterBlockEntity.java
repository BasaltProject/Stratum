// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.basaltmc.stratum.bukkit.facade.TileNbt;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class CrafterBlockEntity extends RandomizableContainerBlockEntity implements CraftingContainer {
    public static final int CONTAINER_WIDTH = 3;
    public static final int CONTAINER_HEIGHT = 3;
    public static final int CONTAINER_SIZE = 9;
    public static final int SLOT_DISABLED = 1;
    public static final int SLOT_ENABLED = 0;
    public static final int DATA_TRIGGERED = 9;
    public static final int NUM_DATA = 10;
    public CrafterBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityType.named("crafter"), worldPosition, blockState);
    }

    public CrafterBlockEntity(ServerLevel level, BlockPos pos, CompoundTag nbt) {
        super(level, pos, nbt);
    }

    public int getCraftingTicksRemaining() {
        return nbt().getIntOr("crafting_ticks_remaining", 0);
    }

    public boolean isSlotDisabled(int slot) {
        for (int disabled : nbt().getIntArray("disabled_slots").orElse(new int[0])) {
            if (disabled == slot) {
                return true;
            }
        }
        return false;
    }

    public void setSlotState(int slotId, boolean enabled) {
        if (!slotCanBeDisabled(slotId)) {
            return;
        }
        java.util.List<Integer> disabled = new java.util.ArrayList<>();
        for (int i = 0; i < CONTAINER_SIZE; i++) {
            if (i == slotId ? !enabled : isSlotDisabled(i)) {
                disabled.add(i);
            }
        }
        CompoundTag tag = nbt();
        tag.putIntArray("disabled_slots", disabled.stream().mapToInt(Integer::intValue).toArray());
        load(tag);
    }

    private boolean slotCanBeDisabled(int slotId) {
        return slotId > -1 && slotId < CONTAINER_SIZE && getItem(slotId).isEmpty();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack itemStack) {
        if (isSlotDisabled(slot)) {
            return false;
        }
        ItemStack present = getItem(slot);
        int size = present.getCount();
        if (size >= present.getMaxStackSize()) {
            return false;
        }
        if (present.isEmpty()) {
            return true;
        }
        for (int i = slot + 1; i < CONTAINER_SIZE; i++) {
            if (!isSlotDisabled(i)) {
                ItemStack other = getItem(i);
                if (other.isEmpty() || other.getCount() < size && ItemStack.isSameItemSameComponents(other, present)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void setItem(int slot, ItemStack itemStack) {
        if (isSlotDisabled(slot)) {
            setSlotState(slot, true);
        }
        super.setItem(slot, itemStack);
    }

    public int getWidth() {
        return CONTAINER_WIDTH;
    }

    public int getHeight() {
        return CONTAINER_HEIGHT;
    }

    public void setTriggered(boolean value) {
        CompoundTag tag = nbt();
        tag.putInt("triggered", value ? 1 : 0);
        load(tag);
    }

    public boolean isTriggered() {
        return nbt().getIntOr("triggered", 0) == 1;
    }

    public void setCraftingTicksRemaining(int maxCraftingTicks) {
        CompoundTag tag = nbt();
        tag.putInt("crafting_ticks_remaining", maxCraftingTicks);
        load(tag);
    }

    public int getRedstoneSignal() {
        int count = 0;
        for (int i = 0; i < getContainerSize(); i++) {
            if (!getItem(i).isEmpty() || isSlotDisabled(i)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int getContainerSize() {
        return 9;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.crafter");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new net.minecraft.world.inventory.DispenserMenu(containerId, inventory, this);
    }
    protected ContainerData containerData = null;
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
    }
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
    }
    public boolean isEmpty() {
        return super.isEmpty();
    }
    public ItemStack getItem(int slot) {
        return super.getItem(slot);
    }
    public boolean stillValid(Player player) {
        return super.stillValid(player);
    }
    public NonNullList<ItemStack> getItems() {
        return super.getItems();
    }
    protected void setItems(NonNullList<ItemStack> items) {
        super.setItems(items);
    }
    public void fillStackedContents(StackedItemContents contents) {
    }
    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, CrafterBlockEntity entity) {
    }
}
