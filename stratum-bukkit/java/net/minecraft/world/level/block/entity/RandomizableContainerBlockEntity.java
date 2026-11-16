// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class RandomizableContainerBlockEntity extends BaseContainerBlockEntity implements RandomizableContainer {
    protected ResourceKey<LootTable> lootTable;
    protected long lootTableSeed;

    protected RandomizableContainerBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
    }

    protected RandomizableContainerBlockEntity(ServerLevel level, BlockPos pos, CompoundTag nbt) {
        super(level, pos, nbt);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public ResourceKey<LootTable> getLootTable() {
        String id = nbt().getStringOr("LootTable", "");
        return id.isEmpty() ? null : (ResourceKey) ResourceKey.create(
            (ResourceKey) net.minecraft.core.registries.Registries.LOOT_TABLE, Identifier.parse(id));
    }

    @Override
    public void setLootTable(ResourceKey<LootTable> lootTable) {
        CompoundTag tag = nbt();
        if (lootTable == null) {
            tag.remove("LootTable");
        } else {
            tag.putString("LootTable", lootTable.identifier().toString());
        }
        load(tag);
    }

    @Override
    public long getLootTableSeed() {
        return nbt().getLongOr("LootTableSeed", 0L);
    }

    @Override
    public void setLootTableSeed(long lootTableSeed) {
        CompoundTag tag = nbt();
        tag.putLong("LootTableSeed", lootTableSeed);
        load(tag);
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return readItems(getContainerSize());
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        writeItems(items);
    }
    public boolean isEmpty() {
        return super.isEmpty();
    }
    public ItemStack getItem(int slot) {
        return super.getItem(slot);
    }
    public ItemStack removeItem(int slot, int count) {
        return super.removeItem(slot, count);
    }
    public ItemStack removeItemNoUpdate(int slot) {
        return super.removeItemNoUpdate(slot);
    }
    public void setItem(int slot, ItemStack itemStack) {
        super.setItem(slot, itemStack);
    }
    public boolean canOpen(Player player) {
        return super.canOpen(player);
    }
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return super.createMenu(containerId, inventory, player);
    }
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);
    }
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
    }
    public void removeComponentsFromTag(ValueOutput output) {
        super.removeComponentsFromTag(output);
    }
}
