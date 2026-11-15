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

public class JukeboxBlockEntity extends BlockEntity {
    public JukeboxBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityType.named("jukebox"), worldPosition, blockState);
    }

    public JukeboxBlockEntity(ServerLevel level, BlockPos pos, CompoundTag nbt) {
        super(level, pos, nbt);
    }

    public ItemStack getTheItem() {
        return ItemStack.OPTIONAL_CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE, nbt().getCompoundOrEmpty("RecordItem")).result().orElse(ItemStack.EMPTY);
    }

    public boolean isRecordPlaying() {
        return nbt().getBooleanOr("is_playing", false);
    }

    public int getMaxStackSize() {
        return 1;
    }

    public BlockEntity getContainerBlockEntity() {
        return this;
    }

    public boolean canPlaceItem(int slot, ItemStack itemStack) {
        return itemStack.has(net.minecraft.core.component.DataComponents.JUKEBOX_PLAYABLE) && getTheItem().isEmpty();
    }

    public boolean canTakeItem(net.minecraft.world.Container into, int slot, ItemStack itemStack) {
        return into.hasAnyMatching(ItemStack::isEmpty);
    }

    public int getComparatorOutput() {
        return net.minecraft.world.item.JukeboxSong.fromStack(getTheItem()).map(net.minecraft.core.Holder::value)
            .map(net.minecraft.world.item.JukeboxSong::comparatorOutput).orElse(0);
    }
}
