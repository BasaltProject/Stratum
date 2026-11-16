// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block.entity;

import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.basaltmc.stratum.bukkit.facade.TileNbt;

public class BellBlockEntity extends BlockEntity {
    public BellBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityType.named("bell"), worldPosition, blockState);
    }

    public BellBlockEntity(ServerLevel level, BlockPos pos, CompoundTag nbt) {
        super(level, pos, nbt);
    }

    interface ResonationEndAction {
        void run(Level level, BlockPos pos, List<LivingEntity> nearbyEntities);
    }
    public int ticks;
    public boolean shaking;
    public Direction clickDirection;
    public boolean triggerEvent(int b0, int b1) {
        return super.triggerEvent(b0, b1);
    }
    public static void clientTick(Level level, BlockPos pos, BlockState state, BellBlockEntity entity) {
    }
    public static void serverTick(Level level, BlockPos pos, BlockState state, BellBlockEntity entity) {
    }
    public void onHit(Direction clickDirection) {
    }
}
