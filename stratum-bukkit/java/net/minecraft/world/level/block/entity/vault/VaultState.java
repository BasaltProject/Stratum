// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block.entity.vault;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
public enum VaultState implements StringRepresentable {
    INACTIVE, ACTIVE, UNLOCKING, EJECTING;
    public String getSerializedName() {
        return name().toLowerCase(java.util.Locale.ROOT);
    }
    public int lightLevel() {
        return this == INACTIVE ? 6 : 12;
    }
    public VaultState tickAndGetNext(ServerLevel serverLevel, BlockPos pos, VaultConfig config, VaultServerData serverData, VaultSharedData sharedData) {
        org.basaltmc.stratum.bukkit.WorldEnvNatives.vault(serverLevel.getWorld().getName(), "tick", pos.getX(), pos.getY(), pos.getZ(), "");
        return valueOf(serverLevel.getBlockState(pos).getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.VAULT_STATE).name());
    }

    public void onTransition(ServerLevel serverLevel, BlockPos pos, VaultState to, VaultConfig config, VaultSharedData sharedData, boolean isOminous) {
    }

    protected void onEnter(ServerLevel serverLevel, BlockPos pos, VaultConfig config, VaultSharedData sharedData, boolean isOminous) {
    }

    protected void onExit(ServerLevel serverLevel, BlockPos pos, VaultConfig config, VaultSharedData sharedData) {
    }

    static enum LightLevel {
        HALF_LIT, LIT;
    }
}
