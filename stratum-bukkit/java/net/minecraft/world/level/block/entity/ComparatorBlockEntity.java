// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.basaltmc.stratum.bukkit.facade.TileNbt;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ComparatorBlockEntity extends BlockEntity {
    public ComparatorBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityType.named("comparator"), worldPosition, blockState);
    }

    public ComparatorBlockEntity(ServerLevel level, BlockPos pos, CompoundTag nbt) {
        super(level, pos, nbt);
    }

    public int getOutputSignal() {
        return nbt().getIntOr("OutputSignal", 0);
    }

    public void setOutputSignal(int outputSignal) {
        TileNbt.update(this, tag -> tag.putInt("OutputSignal", outputSignal));
    }
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
    }
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
    }
}
