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
import net.minecraft.world.Clearable;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class LecternBlockEntity extends BlockEntity implements Clearable, MenuProvider {
    public static final int DATA_PAGE = 0;
    public static final int NUM_DATA = 1;
    public static final int SLOT_BOOK = 0;
    public static final int NUM_SLOTS = 1;
    public LecternBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityType.named("lectern"), worldPosition, blockState);
    }

    public LecternBlockEntity(ServerLevel level, BlockPos pos, CompoundTag nbt) {
        super(level, pos, nbt);
    }

    public ItemStack getBook() {
        return net.minecraft.world.item.ItemStack.OPTIONAL_CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE, nbt().getCompoundOrEmpty("Book"))
            .result().orElse(ItemStack.EMPTY);
    }

    public boolean hasBook() {
        return !getBook().isEmpty();
    }

    public int getPage() {
        return nbt().getIntOr("Page", 0);
    }

    @Override
    public void clearContent() {
        TileNbt.update(this, tag -> {
            tag.remove("Book");
            tag.remove("Page");
        });
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.lectern");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new net.minecraft.world.inventory.LecternMenu(containerId, new net.minecraft.world.SimpleContainer(1),
            new net.minecraft.world.inventory.SimpleContainerData(1));
    }

    public void setBook(ItemStack book) {
        setBook(book, null);
    }

    public void setBook(ItemStack book, Player resolutionContext) {
        TileNbt.update(this, tag -> {
            tag.remove("Book");
            if (!book.isEmpty()) {
                ItemStack.OPTIONAL_CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE, book).result().ifPresent(encoded -> tag.put("Book", encoded));
            }
            tag.putInt("Page", 0);
        });
    }

    private static int pageCount(ItemStack book) {
        net.minecraft.world.item.component.WrittenBookContent written = book.get(net.minecraft.core.component.DataComponents.WRITTEN_BOOK_CONTENT);
        if (written != null) {
            return written.pages().size();
        }
        net.minecraft.world.item.component.WritableBookContent writable = book.get(net.minecraft.core.component.DataComponents.WRITABLE_BOOK_CONTENT);
        return writable != null ? writable.pages().size() : 0;
    }

    public int getRedstoneSignal() {
        int pages = pageCount(getBook());
        float progress = pages > 1 ? (float) getPage() / ((float) pages - 1.0F) : 1.0F;
        return net.minecraft.util.Mth.floor(progress * 14.0F) + (hasBook() ? 1 : 0);
    }
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
    }
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
    }
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
    }
}
