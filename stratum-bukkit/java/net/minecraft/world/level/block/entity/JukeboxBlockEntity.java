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
import net.minecraft.world.Container;
import net.minecraft.world.item.JukeboxSongPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.ticks.ContainerSingleItem;

public class JukeboxBlockEntity extends BlockEntity implements ContainerSingleItem.BlockContainerSingleItem {
    public static final String SONG_ITEM_TAG_ID = "RecordItem";
    public static final String TICKS_SINCE_SONG_STARTED_TAG_ID = "ticks_since_song_started";
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
    public JukeboxSongPlayer getSongPlayer() {
        throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.level.block.entity.JukeboxBlockEntity#getSongPlayer");
    }
    public void onSongChanged() {
    }
    public void popOutTheItem() {
    }
    public static void tick(Level level, BlockPos blockPos, BlockState blockState, JukeboxBlockEntity jukebox) {
    }
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
    }
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
    }
    public ItemStack splitTheItem(int count) {
        throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.level.block.entity.JukeboxBlockEntity#splitTheItem");
    }
    public void setTheItem(ItemStack itemStack) {
    }
    public void setRemoved() {
        super.setRemoved();
    }
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
    }
    public void setSongItemWithoutPlaying(ItemStack itemStack) {
    }
    public void tryForcePlaySong() {
    }
}
