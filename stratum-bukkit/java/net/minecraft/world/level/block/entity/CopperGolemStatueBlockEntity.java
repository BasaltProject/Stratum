// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.basaltmc.stratum.bukkit.facade.TileNbt;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.animal.golem.CopperGolem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CopperGolemStatueBlock;

public class CopperGolemStatueBlockEntity extends BlockEntity {
    public CopperGolemStatueBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityType.named("copper_golem_statue"), worldPosition, blockState);
    }

    public CopperGolemStatueBlockEntity(ServerLevel level, BlockPos pos, CompoundTag nbt) {
        super(level, pos, nbt);
    }
    public void createStatue(CopperGolem copperGolem) {
    }
    public CopperGolem removeStatue(BlockState state) {
        throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.level.block.entity.CopperGolemStatueBlockEntity#removeStatue");
    }
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }
    public ItemStack getItem(ItemStack itemStack, CopperGolemStatueBlock.Pose pose) {
        throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.level.block.entity.CopperGolemStatueBlockEntity#getItem");
    }
}
