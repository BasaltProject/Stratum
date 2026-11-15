// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block.entity;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.ticks.ContainerSingleItem;

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

public class DecoratedPotBlockEntity extends BlockEntity {
    public static final String TAG_SHERDS = "sherds";
    public static final String TAG_ITEM = "item";
    public static final int EVENT_POT_WOBBLES = 1;
    public DecoratedPotBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityType.named("decorated_pot"), worldPosition, blockState);
    }

    public DecoratedPotBlockEntity(ServerLevel level, BlockPos pos, CompoundTag nbt) {
        super(level, pos, nbt);
    }

    public ItemStack getTheItem() {
        return ItemStack.OPTIONAL_CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE, nbt().getCompoundOrEmpty("item")).result().orElse(ItemStack.EMPTY);
    }

    public long wobbleStartedAtTick;
    public WobbleStyle lastWobbleStyle;
    public net.minecraft.core.Direction getDirection() {
        return getBlockState().getValue(net.minecraft.world.level.block.DecoratedPotBlock.HORIZONTAL_FACING);
    }
    public PotDecorations getDecorations() {
        net.minecraft.nbt.Tag stored = nbt().get("sherds");
        return stored == null ? PotDecorations.EMPTY : PotDecorations.CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE, stored).result().orElse(PotDecorations.EMPTY);
    }
    public BlockEntity getContainerBlockEntity() {
        return this;
    }
    public void wobble(WobbleStyle wobbleStyle) {
        if (getLevel() != null && !getLevel().isClientSide()) {
            getLevel().blockEvent(getBlockPos(), getBlockState().getBlock(), 1, wobbleStyle.ordinal());
        }
    }
    @Override
    public boolean triggerEvent(int event, int data) {
        if (getLevel() != null && event == 1 && data >= 0 && data < WobbleStyle.values().length) {
            wobbleStartedAtTick = getLevel().getGameTime();
            lastWobbleStyle = WobbleStyle.values()[data];
            return true;
        }
        return super.triggerEvent(event, data);
    }
    public static enum WobbleStyle {
        POSITIVE(7), NEGATIVE(10);
        public final int duration;
        private WobbleStyle(int duration) {
            this.duration = duration;
        }
    }
}
