// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block.entity;

import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.basaltmc.stratum.bukkit.facade.TileNbt;

public class JigsawBlockEntity extends BlockEntity {
    public JigsawBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityType.named("jigsaw"), worldPosition, blockState);
    }

    public JigsawBlockEntity(ServerLevel level, BlockPos pos, CompoundTag nbt) {
        super(level, pos, nbt);
    }

    public net.minecraft.resources.Identifier getName() {
        return net.minecraft.resources.Identifier.parse(nbt().getStringOr("name", "minecraft:empty"));
    }

    public net.minecraft.resources.Identifier getTarget() {
        return net.minecraft.resources.Identifier.parse(nbt().getStringOr("target", "minecraft:empty"));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public net.minecraft.resources.ResourceKey<net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool> getPool() {
        return (net.minecraft.resources.ResourceKey) net.minecraft.resources.ResourceKey.create(
            (net.minecraft.resources.ResourceKey) net.minecraft.core.registries.Registries.TEMPLATE_POOL,
            net.minecraft.resources.Identifier.parse(nbt().getStringOr("pool", "minecraft:empty")));
    }
    public int getPlacementPriority() {
        return nbt().getIntOr("placement_priority", 0);
    }
    public int getSelectionPriority() {
        return nbt().getIntOr("selection_priority", 0);
    }
    public void setName(net.minecraft.resources.Identifier name) {
        org.basaltmc.stratum.bukkit.facade.TileNbt.update(this, tag -> tag.putString("name", name.toString()));
    }
    public void setTarget(net.minecraft.resources.Identifier target) {
        org.basaltmc.stratum.bukkit.facade.TileNbt.update(this, tag -> tag.putString("target", target.toString()));
    }
    public void setPool(net.minecraft.resources.ResourceKey<net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool> pool) {
        org.basaltmc.stratum.bukkit.facade.TileNbt.update(this, tag -> tag.putString("pool", pool.identifier().toString()));
    }
    public void setFinalState(String finalState) {
        org.basaltmc.stratum.bukkit.facade.TileNbt.update(this, tag -> tag.putString("final_state", finalState));
    }
    public void setJoint(JointType joint) {
        org.basaltmc.stratum.bukkit.facade.TileNbt.update(this, tag -> tag.putString("joint", joint.getSerializedName()));
    }
    public void setPlacementPriority(int placementPriority) {
        org.basaltmc.stratum.bukkit.facade.TileNbt.update(this, tag -> tag.putInt("placement_priority", placementPriority));
    }
    public void setSelectionPriority(int selectionPriority) {
        org.basaltmc.stratum.bukkit.facade.TileNbt.update(this, tag -> tag.putInt("selection_priority", selectionPriority));
    }

    public JointType getJoint() {
        return nbt().getStringOr("joint", "rollable").equals("aligned") ? JointType.ALIGNED : JointType.ROLLABLE;
    }

    public String getFinalState() {
        return nbt().getStringOr("final_state", "minecraft:air");
    }

    public static enum JointType implements StringRepresentable {
        ROLLABLE, ALIGNED;
        public static final StringRepresentable.EnumCodec<JointType> CODEC = net.minecraft.util.StringRepresentable.fromEnum(net.minecraft.world.level.block.entity.JigsawBlockEntity.JointType::values);
        public String getSerializedName() {
            return name().toLowerCase(java.util.Locale.ROOT);
        }
        public Component getTranslatedName() {
            return Component.translatable("jigsaw_block.joint." + getSerializedName());
        }
    }
}
