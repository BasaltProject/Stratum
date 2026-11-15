package org.basaltmc.stratum.bukkit.inv;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/** The two halves of a double chest as one inventory, the left chest's slots first. */
public final class DoubleBlockInventory extends BlockInventory implements DoubleChestInventory {
    private final BlockInventory left;
    private final BlockInventory right;

    public DoubleBlockInventory(BlockInventory left, BlockInventory right) {
        super(left.worldName(), left.blockX(), left.blockY(), left.blockZ(), InventoryType.CHEST, left.getSize() + right.getSize(),
            null);
        this.left = left;
        this.right = right;
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot < left.getSize() ? left.getItem(slot) : right.getItem(slot - left.getSize());
    }

    @Override
    public void setItem(int slot, ItemStack item) {
        if (slot < left.getSize()) {
            left.setItem(slot, item);
        } else {
            right.setItem(slot - left.getSize(), item);
        }
    }

    @Override
    public Inventory getLeftSide() {
        return left;
    }

    @Override
    public Inventory getRightSide() {
        return right;
    }

    @Override
    public DoubleChest getHolder() {
        return new DoubleChest(this);
    }

    @Override
    public DoubleChest getHolder(boolean useSnapshot) {
        return getHolder();
    }

    @Override
    public Location getLocation() {
        World found = Bukkit.getWorld(world);
        return found == null ? null : new Location(found, (left.blockX() + right.blockX()) / 2.0 + 0.5,
            (left.blockY() + right.blockY()) / 2.0 + 0.5, (left.blockZ() + right.blockZ()) / 2.0 + 0.5);
    }

    @Override
    boolean sameBlock(BlockInventory other) {
        return other == this || other.sameBlock(left) || other.sameBlock(right);
    }
}
