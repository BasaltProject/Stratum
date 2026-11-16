// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.basaltmc.stratum.bukkit.facade.TileNbt;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class BannerBlockEntity extends BlockEntity implements Nameable {
    public static final int MAX_PATTERNS = 6;
    public BannerBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityType.named("banner"), worldPosition, blockState);
    }

    public BannerBlockEntity(ServerLevel level, BlockPos pos, CompoundTag nbt) {
        super(level, pos, nbt);
    }

    @Override
    public Component getCustomName() {
        return TileNbt.component(this, "CustomName");
    }

    public void setCustomName(Component name) {
        TileNbt.setComponent(this, "CustomName", name);
    }

    @Override
    public Component getName() {
        Component custom = getCustomName();
        return custom != null ? custom : Component.translatable("block.minecraft.banner");
    }

    @Override
    public Component getDisplayName() {
        return getName();
    }

    @Override
    public boolean hasCustomName() {
        return getCustomName() != null;
    }

    public net.minecraft.world.level.block.entity.BannerPatternLayers getPatterns() {
        net.minecraft.nbt.Tag stored = nbt().get("patterns");
        return stored == null ? net.minecraft.world.level.block.entity.BannerPatternLayers.EMPTY
            : net.minecraft.core.component.DataComponents.BANNER_PATTERNS.codecOrThrow().parse(net.minecraft.nbt.NbtOps.INSTANCE, stored)
                .result().orElse(net.minecraft.world.level.block.entity.BannerPatternLayers.EMPTY);
    }

    public net.minecraft.world.item.DyeColor getBaseColor() {
        return getBlockState().getBlock() instanceof net.minecraft.world.level.block.AbstractBannerBlock banner ? banner.getColor()
            : net.minecraft.world.item.DyeColor.WHITE;
    }

    public net.minecraft.world.item.ItemStack getItem() {
        net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(getBlockState().getBlock());
        stack.set(net.minecraft.core.component.DataComponents.BANNER_PATTERNS, getPatterns());
        Component name = getCustomName();
        if (name != null) {
            stack.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, name);
        }
        return stack;
    }
    public BannerBlockEntity(BlockPos worldPosition, BlockState blockState, DyeColor color) {
        super();
    }
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
    }
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
    }
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return super.getUpdateTag(registries);
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
