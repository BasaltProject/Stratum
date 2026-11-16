// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block.entity;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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

public class SculkShriekerBlockEntity extends BlockEntity implements GameEventListener.Provider<VibrationSystem.Listener>, VibrationSystem {
    public SculkShriekerBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityType.named("sculk_shrieker"), worldPosition, blockState);
    }

    public SculkShriekerBlockEntity(ServerLevel level, BlockPos pos, CompoundTag nbt) {
        super(level, pos, nbt);
    }

    public void tryShriek(ServerLevel level, ServerPlayer player) {
        org.bukkit.block.SculkShrieker shrieker = bukkit(org.bukkit.block.SculkShrieker.class);
        if (shrieker != null && player != null && player.getBukkitEntity() instanceof org.bukkit.entity.Player bukkitPlayer) {
            shrieker.tryShriek(bukkitPlayer);
        }
    }

    public boolean canRespond() {
        return getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.CAN_SUMMON);
    }

    class VibrationUser implements VibrationSystem.User {
        public VibrationUser() {
            
        }
        public int getListenerRadius() {
            return 8;
        }
        public PositionSource getPositionSource() {
            return new net.minecraft.world.level.gameevent.BlockPositionSource(getBlockPos());
        }
        public TagKey<GameEvent> getListenableEvents() {
            return net.minecraft.tags.GameEventTags.SHRIEKER_CAN_LISTEN;
        }
        public boolean canReceiveVibration(ServerLevel level, BlockPos pos, Holder<GameEvent> event, GameEvent.Context context) {
            return !pos.equals(getBlockPos());
        }
        public void onReceiveVibration(ServerLevel level, BlockPos pos, Holder<GameEvent> event, Entity sourceEntity, Entity projectileOwner, float receivingDistance) {
            Entity cause = sourceEntity instanceof net.minecraft.server.level.ServerPlayer ? sourceEntity : projectileOwner;
            if (cause instanceof net.minecraft.server.level.ServerPlayer player) {
                BlockPos at = getBlockPos();
                org.basaltmc.stratum.bukkit.WorldEnvNatives.sculk(level.getWorld().getName(), "shriek", at.getX(), at.getY(), at.getZ(), 0, 0, 0, 0,
                    player.getUUID().toString());
            }
        }
        public void onDataChanged() {
            setChanged();
        }
        public boolean requiresAdjacentChunksToBeTicking() {
            return true;
        }
    }
    public VibrationSystem.Data getVibrationData() {
        throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.level.block.entity.SculkShriekerBlockEntity#getVibrationData");
    }
    public VibrationSystem.User getVibrationUser() {
        throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.level.block.entity.SculkShriekerBlockEntity#getVibrationUser");
    }
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
    }
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
    }
    public static ServerPlayer tryGetPlayer(Entity sourceEntity) {
        throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.level.block.entity.SculkShriekerBlockEntity#tryGetPlayer");
    }
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
    }
    public void tryRespond(ServerLevel level) {
    }
    public VibrationSystem.Listener getListener() {
        throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.level.block.entity.SculkShriekerBlockEntity#getListener");
    }
}
