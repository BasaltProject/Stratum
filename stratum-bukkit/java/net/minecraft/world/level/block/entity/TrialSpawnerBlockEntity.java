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
import net.minecraft.core.HolderLookup;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class TrialSpawnerBlockEntity extends BlockEntity implements TrialSpawner.StateAccessor, Spawner {
    public TrialSpawnerBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityType.named("trial_spawner"), worldPosition, blockState);
    }

    public TrialSpawnerBlockEntity(ServerLevel level, BlockPos pos, CompoundTag nbt) {
        super(level, pos, nbt);
    }

    public boolean isOminous() {
        return getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.OMINOUS);
    }

    public java.util.Collection<org.bukkit.entity.Player> getTrackedPlayers() {
        org.bukkit.block.TrialSpawner spawner = bukkit(org.bukkit.block.TrialSpawner.class);
        return spawner == null ? java.util.List.of() : spawner.getTrackedPlayers();
    }

    public int getRequiredPlayerRange() {
        org.bukkit.block.TrialSpawner spawner = bukkit(org.bukkit.block.TrialSpawner.class);
        return spawner == null ? 14 : spawner.getRequiredPlayerRange();
    }

    public int getCooldownLength() {
        org.bukkit.block.TrialSpawner spawner = bukkit(org.bukkit.block.TrialSpawner.class);
        return spawner == null ? 0 : spawner.getCooldownLength();
    }
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
    }
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
    }
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return super.getUpdateTag(registries);
    }
    public void setEntityId(EntityType<?> type, RandomSource random) {
    }
    public void setEntityData(TypedEntityData<EntityType<?>> entityData, RandomSource random) {
    }
    public TrialSpawner getTrialSpawner() {
        throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.level.block.entity.TrialSpawnerBlockEntity#getTrialSpawner");
    }
    public TrialSpawnerState getState() {
        throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.level.block.entity.TrialSpawnerBlockEntity#getState");
    }
    public void setState(Level level, TrialSpawnerState state) {
    }
    public void markUpdated() {
    }
}
