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
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

public class ShulkerBoxBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer {
    public static final int COLUMNS = 9;
    public static final int ROWS = 3;
    public static final int CONTAINER_SIZE = 27;
    public static final int EVENT_SET_OPEN_COUNT = 1;
    public static final int OPENING_TICK_LENGTH = 10;
    public static final float MAX_LID_HEIGHT = 0.5F;
    public static final float MAX_LID_ROTATION = 270.0F;
    protected ShulkerBoxBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
    }

    public ShulkerBoxBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityType.named("shulker_box"), worldPosition, blockState);
    }

    public ShulkerBoxBlockEntity(ServerLevel level, BlockPos pos, CompoundTag nbt) {
        super(level, pos, nbt);
    }

    @Override
    public int getContainerSize() {
        return 27;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.shulkerBox");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new net.minecraft.world.inventory.ShulkerBoxMenu(containerId, inventory, this);
    }

    @Override
    public int[] getSlotsForFace(net.minecraft.core.Direction direction) {
        int[] slots = new int[getContainerSize()];
        java.util.Arrays.setAll(slots, i -> i);
        return slots;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, net.minecraft.world.item.ItemStack itemStack, net.minecraft.core.Direction direction) {
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, net.minecraft.world.item.ItemStack itemStack, net.minecraft.core.Direction direction) {
        return true;
    }

    @Override
    public void startOpen(net.minecraft.world.entity.ContainerUser user) {
        org.basaltmc.stratum.bukkit.facade.LidControl.set(this, true);
    }

    @Override
    public void stopOpen(net.minecraft.world.entity.ContainerUser user) {
        org.basaltmc.stratum.bukkit.facade.LidControl.set(this, false);
    }

    public AnimationStatus getAnimationStatus() {
        return org.basaltmc.stratum.bukkit.facade.LidControl.openness(this) > 0.0F ? AnimationStatus.OPENED : AnimationStatus.CLOSED;
    }

    public float getProgress(float partialTicks) {
        return org.basaltmc.stratum.bukkit.facade.LidControl.openness(this);
    }

    public boolean isClosed() {
        return getAnimationStatus() == AnimationStatus.CLOSED;
    }

    public net.minecraft.world.item.DyeColor getColor() {
        String path = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(getBlockState().getBlock()).getPath();
        return path.endsWith("_shulker_box") ? net.minecraft.world.item.DyeColor.byName(path.substring(0, path.length() - 12), null) : null;
    }

    public static enum AnimationStatus {
        CLOSED, OPENING, OPENED, CLOSING;
    }
    public ShulkerBoxBlockEntity(DyeColor color, BlockPos worldPosition, BlockState blockState) {
        super((net.minecraft.world.level.block.entity.BlockEntityType) null, (net.minecraft.core.BlockPos) null, (net.minecraft.world.level.block.state.BlockState) null);
    }
    public static void tick(Level level, BlockPos pos, BlockState state, ShulkerBoxBlockEntity entity) {
    }
    public AABB getBoundingBox(BlockState state) {
        throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity#getBoundingBox");
    }
    public boolean triggerEvent(int b0, int b1) {
        return super.triggerEvent(b0, b1);
    }
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
    }
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
    }
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
    }
    public void loadFromTag(ValueInput input) {
    }
    protected NonNullList<ItemStack> getItems() {
        return super.getItems();
    }
    protected void setItems(NonNullList<ItemStack> items) {
        super.setItems(items);
    }
}
