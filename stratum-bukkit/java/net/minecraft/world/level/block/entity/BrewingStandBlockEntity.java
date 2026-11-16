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
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.component.BrewingFuel;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class BrewingStandBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
    public static final int DATA_TOTAL_BREW_TIME = 2;
    public static final int DATA_TOTAL_FUEL_USES = 3;
    public static final int BREWING_TIME_SECONDS = 20;
    public static final int FUEL_USES = 20;
    public static final int DATA_BREW_TIME = 0;
    public static final int DATA_FUEL_USES = 1;
    public static final int NUM_DATA_VALUES = 2;
    private static final int[] kSlotsForUp = {3};
    private static final int[] kSlotsForDown = {0, 1, 2, 3};
    private static final int[] kSlotsForSides = {0, 1, 2, 4};

    public BrewingStandBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityType.named("brewing_stand"), worldPosition, blockState);
    }

    public BrewingStandBlockEntity(ServerLevel level, BlockPos pos, CompoundTag nbt) {
        super(level, pos, nbt);
    }

    public int getBrewTime() {
        return nbt().getShortOr("BrewTime", (short) 0);
    }

    public int getFuel() {
        return nbt().getByteOr("Fuel", (byte) 0);
    }

    @Override
    public int getContainerSize() {
        return 5;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.brewing");
    }

    @Override
    protected net.minecraft.core.NonNullList<ItemStack> getItems() {
        return readItems(5);
    }

    @Override
    protected void setItems(net.minecraft.core.NonNullList<ItemStack> items) {
        writeItems(items);
    }

    @Override
    public int[] getSlotsForFace(Direction direction) {
        return direction == Direction.UP ? kSlotsForUp : direction == Direction.DOWN ? kSlotsForDown : kSlotsForSides;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack itemStack, Direction direction) {
        return canPlaceItem(slot, itemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack itemStack, Direction direction) {
        return slot != 3 || itemStack.is(net.minecraft.world.item.Items.GLASS_BOTTLE);
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new net.minecraft.world.inventory.BrewingStandMenu(containerId, inventory, this, new net.minecraft.world.inventory.SimpleContainerData(2));
    }
    protected ContainerData dataAccess = null;
    protected int getUses(ServerLevel level, BrewingFuel brewingFuel) {
        return (int) 0;
    }
    protected float getSpeedMultiplier(ServerLevel level, BrewingFuel brewingFuel) {
        return (float) 0;
    }
    public static void serverTick(ServerLevel level, BlockPos pos, BlockState selfState, BrewingStandBlockEntity entity) {
    }
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
    }
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
    }
    public boolean canPlaceItem(int slot, ItemStack itemStack) {
        return super.canPlaceItem(slot, itemStack);
    }
}
