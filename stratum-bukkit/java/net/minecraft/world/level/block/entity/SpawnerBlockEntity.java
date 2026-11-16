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
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class SpawnerBlockEntity extends BlockEntity implements Spawner {
    public SpawnerBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityType.named("mob_spawner"), worldPosition, blockState);
    }

    public SpawnerBlockEntity(ServerLevel level, BlockPos pos, CompoundTag nbt) {
        super(level, pos, nbt);
    }

    public int getDelay() {
        return nbt().getShortOr("Delay", (short) 20);
    }

    public int getRequiredPlayerRange() {
        return nbt().getShortOr("RequiredPlayerRange", (short) 16);
    }

    public int getSpawnCount() {
        return nbt().getShortOr("SpawnCount", (short) 4);
    }

    public int getMaxNearbyEntities() {
        return nbt().getShortOr("MaxNearbyEntities", (short) 6);
    }

    public void setEntityId(net.minecraft.world.entity.EntityType<?> type, net.minecraft.util.RandomSource random) {
        CompoundTag entity = new CompoundTag();
        entity.putString("id", net.minecraft.world.entity.EntityType.getKey(type).toString());
        CompoundTag data = new CompoundTag();
        data.put("entity", entity);
        CompoundTag tag = nbt();
        tag.put("SpawnData", data);
        tag.remove("SpawnPotentials");
        load(tag);
    }
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
    }
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
    }
    public static void clientTick(Level level, BlockPos pos, BlockState state, SpawnerBlockEntity entity) {
    }
    public static void serverTick(Level level, BlockPos pos, BlockState state, SpawnerBlockEntity entity) {
    }
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return super.getUpdateTag(registries);
    }
    public boolean triggerEvent(int b0, int b1) {
        return super.triggerEvent(b0, b1);
    }
    public void setEntityData(TypedEntityData<EntityType<?>> data, RandomSource random) {
    }
    public BaseSpawner getSpawner() {
        throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.level.block.entity.SpawnerBlockEntity#getSpawner");
    }
}
