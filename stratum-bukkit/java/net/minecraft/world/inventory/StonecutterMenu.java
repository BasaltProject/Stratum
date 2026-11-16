// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.crafting.SelectableRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;

public class StonecutterMenu extends AbstractContainerMenu {
    public static final int INPUT_SLOT = 0;
    public static final int RESULT_SLOT = 1;
    private final Container container;
    private final ContainerLevelAccess access;
    private final ContainerData data;

    public StonecutterMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, ContainerLevelAccess.NULL);
    }

    public StonecutterMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(MenuType.STONECUTTER, containerId);
        this.container = new SimpleContainer(2);
        this.access = access;
        this.data = new SimpleContainerData(1);
        addSlot(new Slot(container, 0, 20, 33));
        addSlot(new Slot(container, 1, 143, 33) {
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return false;
            }
        });
        addStandardInventorySlots(inventory, 8, 84);
        addDataSlots(this.data);
    }

    public Container getContainer() {
        return container;
    }

    @Override
    public boolean stillValid(Player player) {
        return access == ContainerLevelAccess.NULL || stillValid(access, player, Blocks.STONECUTTER);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            if (!container.getItem(slot).isEmpty() && !isResult(slot)) {
                player.getInventory().add(container.removeItemNoUpdate(slot));
            }
        }
    }

    private static boolean isResult(int slot) {
        for (int result : new int[] {1}) {
            if (result == slot) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        Slot slot = slots.get(slotIndex);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        int own = 2;
        boolean moved = slotIndex < own ? moveItemStackTo(stack, own, slots.size(), true)
            : moveItemStackTo(stack, 0, own, false);
        if (!moved) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return original;
    }
    public int getSelectedRecipeIndex() {
        return (int) 0;
    }
    public SelectableRecipe.SingleInputSet<StonecutterRecipe> getVisibleRecipes() {
        throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.inventory.StonecutterMenu#getVisibleRecipes");
    }
    public int getNumberOfVisibleRecipes() {
        return (int) 0;
    }
    public boolean hasInputItem() {
        return false;
    }
    public boolean clickMenuButton(Player player, int buttonId) {
        return super.clickMenuButton(player, buttonId);
    }
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
    }
    public MenuType<?> getType() {
        return super.getType();
    }
    public void registerUpdateListener(Runnable slotUpdateListener) {
    }
    public boolean canTakeItemForPickAll(ItemStack carried, Slot target) {
        return super.canTakeItemForPickAll(carried, target);
    }
}
