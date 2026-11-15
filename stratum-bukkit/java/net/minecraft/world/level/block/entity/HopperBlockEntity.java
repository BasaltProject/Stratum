// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class HopperBlockEntity extends RandomizableContainerBlockEntity implements net.minecraft.world.level.block.entity.Hopper {
    public static final int MOVE_ITEM_SPEED = 8;
    public static final int HOPPER_CONTAINER_SIZE = 5;
    protected HopperBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
    }

    public HopperBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityType.named("hopper"), worldPosition, blockState);
    }

    public HopperBlockEntity(ServerLevel level, BlockPos pos, CompoundTag nbt) {
        super(level, pos, nbt);
    }

    @Override
    public int getContainerSize() {
        return 5;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.hopper");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new net.minecraft.world.inventory.HopperMenu(containerId, inventory, this);
    }

    @Override
    public double getLevelX() {
        return getBlockPos().getX() + 0.5;
    }

    @Override
    public double getLevelY() {
        return getBlockPos().getY() + 0.5;
    }

    @Override
    public double getLevelZ() {
        return getBlockPos().getZ() + 0.5;
    }

    @Override
    public boolean isGridAligned() {
        return true;
    }

    private static final String kCooldownKey = "TransferCooldown";

    public static boolean suckInItems(net.minecraft.world.level.Level level, Hopper hopper) {
        BlockPos above = BlockPos.containing(hopper.getLevelX(), hopper.getLevelY() + 1.0, hopper.getLevelZ());
        BlockState aboveState = level.getBlockState(above);
        Container source = getContainerAt(level, above, aboveState, hopper.getLevelX(), hopper.getLevelY() + 1.0, hopper.getLevelZ());
        if (source != null) {
            for (int slot : slotsOf(source, Direction.DOWN)) {
                ItemStack stack = source.getItem(slot);
                if (stack.isEmpty() || !canTakeItemFromContainer(hopper, source, stack, slot, Direction.DOWN)) {
                    continue;
                }
                int original = stack.getCount();
                ItemStack result = addItem(source, hopper, source.removeItem(slot, 1), null);
                if (result.isEmpty()) {
                    source.setChanged();
                    return true;
                }
                stack.setCount(original);
                source.setItem(slot, stack);
            }
            return false;
        }
        boolean blocked = hopper.isGridAligned() && aboveState.isCollisionShapeFullBlock(level, above)
            && !aboveState.is(net.minecraft.tags.BlockTags.DOES_NOT_BLOCK_HOPPERS);
        if (!blocked) {
            for (ItemEntity entity : getItemsAtAndAbove(level, hopper)) {
                if (addItem(hopper, entity)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static java.util.List<ItemEntity> getItemsAtAndAbove(net.minecraft.world.level.Level level, Hopper hopper) {
        AABB box = hopper.getSuckAabb().move(hopper.getLevelX() - 0.5, hopper.getLevelY() - 0.5, hopper.getLevelZ() - 0.5);
        return level.getEntitiesOfClass(ItemEntity.class, box, EntitySelector.ENTITY_STILL_ALIVE);
    }

    public static Container getContainerAt(net.minecraft.world.level.Level level, BlockPos pos) {
        return getContainerAt(level, pos, level.getBlockState(pos), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    private static Container getContainerAt(net.minecraft.world.level.Level level, BlockPos pos, BlockState state, double x, double y,
            double z) {
        Container found = blockContainerAt(level, pos, state);
        if (found == null) {
            java.util.List<net.minecraft.world.entity.Entity> holders = level.getEntities((net.minecraft.world.entity.Entity) null,
                new AABB(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5), EntitySelector.CONTAINER_ENTITY_SELECTOR);
            found = holders.isEmpty() ? null : (Container) holders.get(level.getRandom().nextInt(holders.size()));
        }
        return found;
    }

    private static Container blockContainerAt(net.minecraft.world.level.Level level, BlockPos pos, BlockState state) {
        net.minecraft.world.level.block.Block block = state.getBlock();
        if (block instanceof WorldlyContainerHolder holder) {
            return holder.getContainer(state, (LevelAccessor) level, pos);
        }
        if (state.hasBlockEntity() && level.getBlockEntity(pos) instanceof Container container) {
            if (container instanceof ChestBlockEntity && block instanceof ChestBlock chest) {
                return ChestBlock.getContainer(chest, state, level, pos, true);
            }
            return container;
        }
        return null;
    }

    private static int[] slotsOf(Container container, Direction face) {
        if (container instanceof WorldlyContainer sided) {
            return sided.getSlotsForFace(face);
        }
        int[] all = new int[container.getContainerSize()];
        for (int i = 0; i < all.length; i++) {
            all[i] = i;
        }
        return all;
    }

    private static boolean canTakeItemFromContainer(Container into, Container from, ItemStack stack, int slot, Direction face) {
        return from.canTakeItem(into, slot, stack) && (!(from instanceof WorldlyContainer sided) || sided.canTakeItemThroughFace(slot, stack, face));
    }

    private static boolean canPlaceItemInContainer(Container container, ItemStack stack, int slot, Direction face) {
        return container.canPlaceItem(slot, stack)
            && (!(container instanceof WorldlyContainer sided) || sided.canPlaceItemThroughFace(slot, stack, face));
    }

    public static boolean addItem(Container container, ItemEntity entity) {
        ItemStack rest = addItem(null, container, entity.getItem().copy(), null);
        if (rest.isEmpty()) {
            entity.setItem(ItemStack.EMPTY);
            entity.discard();
            return true;
        }
        entity.setItem(rest);
        return false;
    }

    public static ItemStack addItem(Container from, Container container, ItemStack stack, Direction face) {
        if (container instanceof WorldlyContainer sided && face != null) {
            int[] slots = sided.getSlotsForFace(face);
            for (int i = 0; i < slots.length && !stack.isEmpty(); i++) {
                stack = tryMoveInItem(from, container, stack, slots[i], face);
            }
            return stack;
        }
        int size = container.getContainerSize();
        for (int i = 0; i < size && !stack.isEmpty(); i++) {
            stack = tryMoveInItem(from, container, stack, i, face);
        }
        return stack;
    }

    private static ItemStack tryMoveInItem(Container from, Container container, ItemStack stack, int slot, Direction face) {
        ItemStack current = container.getItem(slot);
        if (!canPlaceItemInContainer(container, stack, slot, face)) {
            return stack;
        }
        boolean moved = false;
        boolean wasEmpty = container.isEmpty();
        if (current.isEmpty()) {
            container.setItem(slot, stack);
            stack = ItemStack.EMPTY;
            moved = true;
        } else if (current.getCount() <= current.getMaxStackSize() && ItemStack.isSameItemSameComponents(current, stack)) {
            int count = Math.min(stack.getCount(), stack.getMaxStackSize() - current.getCount());
            stack.shrink(count);
            current.grow(count);
            container.setItem(slot, current);
            moved = count > 0;
        }
        if (moved) {
            if (wasEmpty && container instanceof HopperBlockEntity hopper && hopper.cooldown() <= 8) {
                hopper.setCooldown(8);
            }
            container.setChanged();
        }
        return stack;
    }

    private int cooldown() {
        return nbt().getIntOr(kCooldownKey, -1);
    }

    private void setCooldown(int ticks) {
        CompoundTag tag = nbt();
        tag.putInt(kCooldownKey, ticks);
        load(tag);
    }
}
