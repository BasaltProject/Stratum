// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.basaltmc.stratum.bukkit.facade.TileNbt;
import net.minecraft.core.Direction;

public class TheEndPortalBlockEntity extends BlockEntity {
    public TheEndPortalBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityType.named("end_portal"), worldPosition, blockState);
    }

    protected TheEndPortalBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
    }

    public TheEndPortalBlockEntity(ServerLevel level, BlockPos pos, CompoundTag nbt) {
        super(level, pos, nbt);
    }
    public boolean shouldRenderFace(Direction direction) {
        return false;
    }
}
