// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.basaltmc.stratum.bukkit.facade.TileNbt;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class SkullBlockEntity extends BlockEntity {
    public SkullBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityType.named("skull"), worldPosition, blockState);
    }

    public SkullBlockEntity(ServerLevel level, BlockPos pos, CompoundTag nbt) {
        super(level, pos, nbt);
    }

    public net.minecraft.resources.Identifier getNoteBlockSound() {
        String sound = nbt().getStringOr("note_block_sound", "");
        return sound.isEmpty() ? null : net.minecraft.resources.Identifier.parse(sound);
    }

    public net.minecraft.world.item.component.ResolvableProfile getOwnerProfile() {
        net.minecraft.nbt.Tag stored = nbt().get("profile");
        return stored == null ? null : net.minecraft.world.item.component.ResolvableProfile.CODEC
            .parse(net.minecraft.nbt.NbtOps.INSTANCE, stored).result().orElse(null);
    }
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
    }
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
    }
    public static void animation(Level level, BlockPos pos, BlockState state, SkullBlockEntity entity) {
    }
    public float getAnimation(float a) {
        return (float) 0;
    }
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return super.getUpdateTag(registries);
    }
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);
    }
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
    }
    public void removeComponentsFromTag(ValueOutput output) {
        super.removeComponentsFromTag(output);
    }
}
