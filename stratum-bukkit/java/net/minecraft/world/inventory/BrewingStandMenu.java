// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.minecraft.world.item.crafting.RecipePropertySet;

public class BrewingStandMenu extends AbstractContainerMenu {
    private static final int BOTTLE_SLOT_START = 0;
    private static final int BOTTLE_SLOT_END = 2;
    private static final int INGREDIENT_SLOT = 3;
    private static final int FUEL_SLOT = 4;
    private static final int SLOT_COUNT = 5;
    private final Container brewingStand;
    private final ContainerData brewingStandData;

    public BrewingStandMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(SLOT_COUNT), new SimpleContainerData(2));
    }

    public BrewingStandMenu(int containerId, Inventory inventory, Container brewingStand, ContainerData brewingStandData) {
        super(MenuType.BREWING_STAND, containerId);
        checkContainerSize(brewingStand, SLOT_COUNT);
        checkContainerDataCount(brewingStandData, 2);
        this.brewingStand = brewingStand;
        this.brewingStandData = brewingStandData;
        addSlot(new Slot(brewingStand, 0, 56, 51));
        addSlot(new Slot(brewingStand, 1, 79, 58));
        addSlot(new Slot(brewingStand, 2, 102, 51));
        addSlot(new Slot(brewingStand, INGREDIENT_SLOT, 79, 17));
        addSlot(new Slot(brewingStand, FUEL_SLOT, 17, 17));
        addStandardInventorySlots(inventory, 8, 84);
        addDataSlots(brewingStandData);
    }

    public int getFuel() {
        return brewingStandData.get(1);
    }

    public int getBrewingTicks() {
        return brewingStandData.get(0);
    }

    @Override
    public boolean stillValid(Player player) {
        return brewingStand.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        Slot slot = slots.get(slotIndex);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        boolean moved;
        if (slotIndex < SLOT_COUNT) {
            moved = moveItemStackTo(stack, SLOT_COUNT, slots.size(), true);
        } else {
            moved = moveItemStackTo(stack, FUEL_SLOT, FUEL_SLOT + 1, false) || moveItemStackTo(stack, INGREDIENT_SLOT, FUEL_SLOT, false)
                || moveItemStackTo(stack, BOTTLE_SLOT_START, BOTTLE_SLOT_END + 1, false);
        }
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
    public int getTotalFuel() {
        return (int) 0;
    }
    public int getTotalBrewingTicks() {
        return (int) 0;
    }
    static class PotionSlot extends Slot {
        public PotionSlot(RecipeAccess recipeAccess, Container container, int slot, int x, int y) {
            super((net.minecraft.world.Container) null, (int) 0, (int) 0, (int) 0);
        }
        public boolean mayPlace(ItemStack itemStack) {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.inventory.BrewingStandMenu.PotionSlot#mayPlace");
        }
        public int getMaxStackSize() {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.inventory.BrewingStandMenu.PotionSlot#getMaxStackSize");
        }
        public void onTake(Player player, ItemStack carried) {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.inventory.BrewingStandMenu.PotionSlot#onTake");
        }
        public Identifier getNoItemIcon() {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.inventory.BrewingStandMenu.PotionSlot#getNoItemIcon");
        }
    }
    static class IngredientsSlot extends Slot {
        public IngredientsSlot(RecipePropertySet propertySet, Container container, int slot, int x, int y) {
            super((net.minecraft.world.Container) null, (int) 0, (int) 0, (int) 0);
        }
        public boolean mayPlace(ItemStack itemStack) {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.inventory.BrewingStandMenu.IngredientsSlot#mayPlace");
        }
    }
    static class FuelSlot extends Slot {
        public FuelSlot(Container container, int slot, int x, int y) {
            super((net.minecraft.world.Container) null, (int) 0, (int) 0, (int) 0);
        }
        public boolean mayPlace(ItemStack itemStack) {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.inventory.BrewingStandMenu.FuelSlot#mayPlace");
        }
        public static boolean mayPlaceItem(ItemStack itemStack) {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.inventory.BrewingStandMenu.FuelSlot#mayPlaceItem");
        }
        public Identifier getNoItemIcon() {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.inventory.BrewingStandMenu.FuelSlot#getNoItemIcon");
        }
    }
}
