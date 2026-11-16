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
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.level.Level;

public class EnderChestBlockEntity extends BlockEntity implements LidBlockEntity {
    public EnderChestBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityType.named("ender_chest"), worldPosition, blockState);
    }

    public EnderChestBlockEntity(ServerLevel level, BlockPos pos, CompoundTag nbt) {
        super(level, pos, nbt);
    }

    public float getOpenNess(float a) {
        return org.basaltmc.stratum.bukkit.facade.LidControl.openness(this);
    }

    public void startOpen(net.minecraft.world.entity.ContainerUser user) {
        org.basaltmc.stratum.bukkit.facade.LidControl.set(this, true);
    }

    public void stopOpen(net.minecraft.world.entity.ContainerUser user) {
        org.basaltmc.stratum.bukkit.facade.LidControl.set(this, false);
    }
    public static void lidAnimateTick(Level level, BlockPos pos, BlockState state, EnderChestBlockEntity entity) {
    }
    public boolean triggerEvent(int b0, int b1) {
        return super.triggerEvent(b0, b1);
    }
    public boolean stillValid(Player player) {
        return false;
    }
    public void recheckOpen() {
    }
}
