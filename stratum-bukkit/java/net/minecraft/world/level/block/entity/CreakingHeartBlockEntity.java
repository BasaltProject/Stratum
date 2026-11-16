// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.basaltmc.stratum.bukkit.facade.TileNbt;
import java.util.UUID;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class CreakingHeartBlockEntity extends BlockEntity {
    public static final int CREAKING_ROAMING_RADIUS = 32;
    public CreakingHeartBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityType.named("creaking_heart"), worldPosition, blockState);
    }

    public CreakingHeartBlockEntity(ServerLevel level, BlockPos pos, CompoundTag nbt) {
        super(level, pos, nbt);
    }

    public boolean isNatural() {
        return getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.NATURAL);
    }
    public static void serverTick(Level level, BlockPos pos, BlockState state, CreakingHeartBlockEntity entity) {
    }
    public void setCreakingInfo(Creaking creaking) {
    }
    public void setCreakingInfo(UUID uuid) {
    }
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return super.getUpdateTag(registries);
    }
    public void creakingHurt() {
    }
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
    }
    public void removeProtector(DamageSource damageSource) {
    }
    public boolean isProtector(Creaking creaking) {
        return false;
    }
    public int getAnalogOutputSignal() {
        return (int) 0;
    }
    public int computeAnalogOutputSignal() {
        return (int) 0;
    }
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
    }
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
    }
}
