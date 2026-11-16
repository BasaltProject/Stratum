// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.basaltmc.stratum.bukkit.facade.TileNbt;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootTable;

public class BrushableBlockEntity extends BlockEntity {
    public BrushableBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityType.named("brushable_block"), worldPosition, blockState);
    }

    public BrushableBlockEntity(ServerLevel level, BlockPos pos, CompoundTag nbt) {
        super(level, pos, nbt);
    }

    public net.minecraft.world.item.ItemStack getItem() {
        return net.minecraft.world.item.ItemStack.OPTIONAL_CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE, nbt().getCompoundOrEmpty("item"))
            .result().orElse(net.minecraft.world.item.ItemStack.EMPTY);
    }
    public boolean brush(long gameTime, ServerLevel level, LivingEntity user, Direction direction, ItemStack brush) {
        return false;
    }
    public void checkReset(ServerLevel level) {
    }
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return super.getUpdateTag(registries);
    }
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
    }
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
    }
    public void setLootTable(ResourceKey<LootTable> lootTable, long seed) {
    }
    public Direction getHitDirection() {
        throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.level.block.entity.BrushableBlockEntity#getHitDirection");
    }
}
