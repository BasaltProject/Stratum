// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.entity.player;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetPlayerInventoryPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.Container;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.inventory.CraftItemStack;

/** The player's slots as a container, backed by the live Bukkit player inventory (0-35 storage, 36-39 armor, 40 offhand). */
public class Inventory implements Container, Nameable {
    public static final int POP_TIME_DURATION = 5;
    public static final int INVENTORY_SIZE = 36;
    public static final int SELECTION_SIZE = 9;
    public static final int SLOT_OFFHAND = 40;
    public static final int SLOT_BODY_ARMOR = 41;
    public static final int SLOT_SADDLE = 42;
    public static final int NOT_FOUND_INDEX = -1;
    public static final Int2ObjectMap<EquipmentSlot> EQUIPMENT_SLOT_MAPPING = new Int2ObjectArrayMap<>(Map.of(
        EquipmentSlot.FEET.getIndex(36), EquipmentSlot.FEET, EquipmentSlot.LEGS.getIndex(36), EquipmentSlot.LEGS,
        EquipmentSlot.CHEST.getIndex(36), EquipmentSlot.CHEST, EquipmentSlot.HEAD.getIndex(36), EquipmentSlot.HEAD,
        SLOT_OFFHAND, EquipmentSlot.OFFHAND, SLOT_BODY_ARMOR, EquipmentSlot.BODY, SLOT_SADDLE, EquipmentSlot.SADDLE));
    public final Player player;
    private final org.bukkit.inventory.PlayerInventory backing;
    private final ItemStack[] unmapped = {ItemStack.EMPTY, ItemStack.EMPTY};
    private int timesChanged;

    public Inventory(Player player, org.bukkit.inventory.PlayerInventory backing) {
        this.player = player;
        this.backing = backing;
    }

    public int getSelectedSlot() {
        return backing.getHeldItemSlot();
    }

    public void setSelectedSlot(int selected) {
        if (!isHotbarSlot(selected)) {
            throw new IllegalArgumentException("Invalid selected slot");
        }
        backing.setHeldItemSlot(selected);
    }

    public ItemStack setSelectedItem(ItemStack itemStack) {
        ItemStack previous = getSelectedItem();
        setItem(getSelectedSlot(), itemStack);
        return previous;
    }

    public static int getSelectionSize() {
        return SELECTION_SIZE;
    }

    public static boolean isHotbarSlot(int slot) {
        return slot >= 0 && slot < SELECTION_SIZE;
    }

    public ItemStack getSelectedItem() {
        return getItem(getSelectedSlot());
    }

    public boolean add(ItemStack itemStack) {
        return add(-1, itemStack);
    }

    public boolean add(int slot, ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return false;
        }
        if (itemStack.isDamaged()) {
            if (slot == -1) {
                slot = getFreeSlot();
            }
            if (slot >= 0) {
                ItemStack placed = itemStack.copyAndClear();
                placed.setPopTime(POP_TIME_DURATION);
                setItem(slot, placed);
                return true;
            }
            if (player.hasInfiniteMaterials()) {
                itemStack.setCount(0);
                return true;
            }
            return false;
        }
        int lastSize;
        do {
            lastSize = itemStack.getCount();
            itemStack.setCount(slot == -1 ? addResource(itemStack) : addResource(slot, itemStack));
        } while (!itemStack.isEmpty() && itemStack.getCount() < lastSize);
        if (itemStack.getCount() == lastSize && player.hasInfiniteMaterials()) {
            itemStack.setCount(0);
            return true;
        }
        return itemStack.getCount() < lastSize;
    }

    private int addResource(ItemStack itemStack) {
        int slot = getSlotWithRemainingSpace(itemStack);
        if (slot == NOT_FOUND_INDEX) {
            slot = getFreeSlot();
        }
        return slot == NOT_FOUND_INDEX ? itemStack.getCount() : addResource(slot, itemStack);
    }

    private int addResource(int slot, ItemStack itemStack) {
        int count = itemStack.getCount();
        ItemStack inSlot = getItem(slot);
        if (inSlot.isEmpty()) {
            inSlot = itemStack.copyWithCount(0);
        }
        int toAdd = Math.min(count, getMaxStackSize(inSlot) - inSlot.getCount());
        if (toAdd == 0) {
            return count;
        }
        inSlot.grow(toAdd);
        inSlot.setPopTime(POP_TIME_DURATION);
        setItem(slot, inSlot);
        return count - toAdd;
    }

    public void addAndPickItem(ItemStack itemStack) {
        setSelectedSlot(getSuitableHotbarSlot());
        ItemStack selected = getSelectedItem();
        if (!selected.isEmpty()) {
            int freeSlot = getFreeSlot();
            if (freeSlot != NOT_FOUND_INDEX) {
                setItem(freeSlot, selected);
            }
        }
        setItem(getSelectedSlot(), itemStack);
    }

    public void pickSlot(int slot) {
        setSelectedSlot(getSuitableHotbarSlot());
        ItemStack previous = getSelectedItem();
        setItem(getSelectedSlot(), getItem(slot));
        setItem(slot, previous);
    }

    public int findSlotMatchingItem(ItemStack itemStack) {
        for (int slot = 0; slot < INVENTORY_SIZE; slot++) {
            ItemStack held = getItem(slot);
            if (!held.isEmpty() && ItemStack.isSameItemSameComponents(itemStack, held)) {
                return slot;
            }
        }
        return NOT_FOUND_INDEX;
    }

    public int getSuitableHotbarSlot() {
        for (int offset = 0; offset < SELECTION_SIZE; offset++) {
            int index = (getSelectedSlot() + offset) % SELECTION_SIZE;
            if (getItem(index).isEmpty()) {
                return index;
            }
        }
        for (int offset = 0; offset < SELECTION_SIZE; offset++) {
            int index = (getSelectedSlot() + offset) % SELECTION_SIZE;
            if (!getItem(index).isEnchanted()) {
                return index;
            }
        }
        return getSelectedSlot();
    }

    public int clearOrCountMatchingItems(Predicate<ItemStack> predicate, boolean countingOnly, int amountToRemove, Container craftSlots) {
        int count = ContainerHelper.clearOrCountMatchingItems(this, predicate, amountToRemove, countingOnly);
        count += ContainerHelper.clearOrCountMatchingItems(craftSlots, predicate, amountToRemove - count, countingOnly);
        if (player.containerMenu == null) {
            return count;
        }
        ItemStack carried = player.containerMenu.getCarried();
        count += ContainerHelper.clearOrCountMatchingItems(carried, predicate, amountToRemove - count, countingOnly);
        if (!countingOnly && carried.isEmpty()) {
            player.containerMenu.setCarried(ItemStack.EMPTY);
        }
        return count;
    }

    public void tick() {
        for (int slot = 0; slot < INVENTORY_SIZE; slot++) {
            ItemStack itemStack = getItem(slot);
            if (!itemStack.isEmpty()) {
                itemStack.inventoryTick(player.level(), player, slot == getSelectedSlot() ? EquipmentSlot.MAINHAND : null);
            }
        }
    }

    public ClientboundSetPlayerInventoryPacket createInventoryUpdatePacket(int slot) {
        return new ClientboundSetPlayerInventoryPacket(slot, getItem(slot).copy());
    }

    public void removeItem(ItemStack itemStack) {
        for (int slot = 0; slot < getContainerSize(); slot++) {
            if (getItem(slot) == itemStack || ItemStack.matches(getItem(slot), itemStack)) {
                setItem(slot, ItemStack.EMPTY);
                return;
            }
        }
    }

    public void save(ValueOutput.TypedOutputList<ItemStackWithSlot> output) {
        for (int slot = 0; slot < INVENTORY_SIZE; slot++) {
            ItemStack item = getItem(slot);
            if (!item.isEmpty()) {
                output.add(new ItemStackWithSlot(slot, item));
            }
        }
    }

    public void load(ValueInput.TypedInputList<ItemStackWithSlot> input) {
        for (int slot = 0; slot < INVENTORY_SIZE; slot++) {
            setItem(slot, ItemStack.EMPTY);
        }
        for (ItemStackWithSlot item : input) {
            if (item.isValidInContainer(INVENTORY_SIZE)) {
                setItem(item.slot(), item.stack());
            }
        }
    }

    public void dropAll() {
        for (int slot = 0; slot < INVENTORY_SIZE; slot++) {
            ItemStack itemStack = getItem(slot);
            if (!itemStack.isEmpty()) {
                ItemEntity drop = player.createItemStackToDrop(itemStack, true, false);
                if (drop != null) {
                    player.level().addFreshEntity(drop);
                }
                setItem(slot, ItemStack.EMPTY);
            }
        }
        for (EquipmentSlot equipmentSlot : EQUIPMENT_SLOT_MAPPING.values()) {
            int slot = equipmentSlotIndex(equipmentSlot);
            ItemStack itemStack = getItem(slot);
            if (!itemStack.isEmpty()) {
                ItemEntity drop = player.createItemStackToDrop(itemStack, true, false);
                if (drop != null) {
                    player.level().addFreshEntity(drop);
                }
                setItem(slot, ItemStack.EMPTY);
            }
        }
    }

    private static int equipmentSlotIndex(EquipmentSlot equipmentSlot) {
        for (Int2ObjectMap.Entry<EquipmentSlot> entry : EQUIPMENT_SLOT_MAPPING.int2ObjectEntrySet()) {
            if (entry.getValue() == equipmentSlot) {
                return entry.getIntKey();
            }
        }
        return NOT_FOUND_INDEX;
    }

    public int getTimesChanged() {
        return timesChanged;
    }

    public boolean contains(ItemStack searchStack) {
        for (ItemStack itemStack : this) {
            if (!itemStack.isEmpty() && ItemStack.isSameItemSameComponents(itemStack, searchStack)) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(net.minecraft.tags.TagKey<net.minecraft.world.item.Item> tag) {
        return contains(itemStack -> !itemStack.isEmpty() && itemStack.is(tag));
    }

    public boolean contains(Predicate<ItemStack> predicate) {
        for (ItemStack itemStack : this) {
            if (predicate.test(itemStack)) {
                return true;
            }
        }
        return false;
    }

    public void replaceWith(Inventory other) {
        for (int slot = 0; slot < getContainerSize(); slot++) {
            setItem(slot, other.getItem(slot));
        }
        setSelectedSlot(other.getSelectedSlot());
    }

    public ItemStack removeFromSelected(boolean all) {
        ItemStack selected = getSelectedItem();
        return selected.isEmpty() ? ItemStack.EMPTY : removeItem(getSelectedSlot(), all ? selected.getCount() : 1);
    }

    @Override
    public int getContainerSize() {
        return INVENTORY_SIZE + EQUIPMENT_SLOT_MAPPING.size();
    }

    @Override
    public boolean isEmpty() {
        for (int slot = 0; slot < getContainerSize(); slot++) {
            if (!getItem(slot).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        if (slot >= SLOT_BODY_ARMOR) {
            return slot <= SLOT_SADDLE ? unmapped[slot - SLOT_BODY_ARMOR] : ItemStack.EMPTY;
        }
        return CraftItemStack.asNMSCopy(backing.getItem(slot));
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        ItemStack current = getItem(slot);
        if (current.isEmpty() || count <= 0) {
            return ItemStack.EMPTY;
        }
        ItemStack taken = current.split(count);
        setItem(slot, current);
        return taken;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack current = getItem(slot);
        setItem(slot, ItemStack.EMPTY);
        return current;
    }

    @Override
    public void setItem(int slot, ItemStack itemStack) {
        if (slot >= SLOT_BODY_ARMOR) {
            if (slot <= SLOT_SADDLE) {
                unmapped[slot - SLOT_BODY_ARMOR] = itemStack;
            }
            return;
        }
        backing.setItem(slot, itemStack.isEmpty() ? null : CraftItemStack.asCraftMirror(itemStack));
    }

    @Override
    public void setChanged() {
        timesChanged++;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        backing.clear();
        unmapped[0] = ItemStack.EMPTY;
        unmapped[1] = ItemStack.EMPTY;
    }

    @Override
    public Component getName() {
        return Component.translatable("container.inventory");
    }

    @Override
    public Component getDisplayName() {
        return getName();
    }

    public net.minecraft.core.NonNullList<ItemStack> getNonEquipmentItems() {
        net.minecraft.core.NonNullList<ItemStack> items = net.minecraft.core.NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
        for (int slot = 0; slot < INVENTORY_SIZE; slot++) {
            items.set(slot, getItem(slot));
        }
        return items;
    }

    public int getFreeSlot() {
        for (int slot = 0; slot < INVENTORY_SIZE; slot++) {
            if (getItem(slot).isEmpty()) {
                return slot;
            }
        }
        return NOT_FOUND_INDEX;
    }

    private boolean hasRoomFor(ItemStack held, ItemStack incoming) {
        return !held.isEmpty() && ItemStack.isSameItemSameComponents(held, incoming) && held.isStackable() && held.getCount() < held.getMaxStackSize();
    }

    public int getSlotWithRemainingSpace(ItemStack incoming) {
        for (int slot : new int[] {getSelectedSlot(), SLOT_OFFHAND}) {
            if (hasRoomFor(getItem(slot), incoming)) {
                return slot;
            }
        }
        for (int slot = 0; slot < INVENTORY_SIZE; slot++) {
            if (hasRoomFor(getItem(slot), incoming)) {
                return slot;
            }
        }
        return NOT_FOUND_INDEX;
    }

    public int findSlotMatchingCraftingIngredient(net.minecraft.core.Holder<net.minecraft.world.item.Item> item, ItemStack existing) {
        for (int slot = 0; slot < INVENTORY_SIZE; slot++) {
            ItemStack held = getItem(slot);
            if (!held.isEmpty() && held.is(item) && isUsableForCrafting(held) && (existing.isEmpty() || ItemStack.isSameItemSameComponents(existing, held))) {
                return slot;
            }
        }
        return NOT_FOUND_INDEX;
    }

    public void placeItemBackInInventory(ItemStack stack, net.minecraft.util.Prediction prediction) {
        placeItemBackInInventory(stack, true, prediction);
    }

    public void placeItemBackInInventory(ItemStack stack, boolean sendSlotUpdate, net.minecraft.util.Prediction prediction) {
        while (!stack.isEmpty()) {
            int slot = getSlotWithRemainingSpace(stack);
            if (slot == NOT_FOUND_INDEX) {
                slot = getFreeSlot();
            }
            if (slot == NOT_FOUND_INDEX) {
                player.drop(stack, false, prediction);
                return;
            }
            ItemStack held = getItem(slot);
            int room = stack.getMaxStackSize() - held.getCount();
            ItemStack moved = stack.split(room);
            if (held.isEmpty()) {
                setItem(slot, moved);
            } else {
                held.grow(moved.getCount());
                setItem(slot, held);
            }
        }
    }

    public void fillStackedContents(StackedItemContents contents) {
        for (int slot = 0; slot < INVENTORY_SIZE; slot++) {
            contents.accountSimpleStack(getItem(slot));
        }
    }
    public static boolean isUsableForCrafting(ItemStack itemStack) {
        return !itemStack.isDamaged() && !itemStack.isEnchanted() && !itemStack.has(net.minecraft.core.component.DataComponents.CUSTOM_NAME);
    }
}
