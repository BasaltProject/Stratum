// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.basaltmc.stratum.bukkit.facade.TileNbt;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class EnchantingTableBlockEntity extends BlockEntity implements Nameable {
    public EnchantingTableBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityType.named("enchanting_table"), worldPosition, blockState);
    }

    public EnchantingTableBlockEntity(ServerLevel level, BlockPos pos, CompoundTag nbt) {
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
        return custom != null ? custom : Component.translatable("container.enchant");
    }

    @Override
    public Component getDisplayName() {
        return getName();
    }

    @Override
    public boolean hasCustomName() {
        return getCustomName() != null;
    }
    public int time;
    public float flip;
    public float oFlip;
    public float flipT;
    public float flipA;
    public float open;
    public float oOpen;
    public float rot;
    public float oRot;
    public float tRot;
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
    }
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
    }
    public static void bookAnimationTick(Level level, BlockPos worldPosition, BlockState state, EnchantingTableBlockEntity entity) {
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
