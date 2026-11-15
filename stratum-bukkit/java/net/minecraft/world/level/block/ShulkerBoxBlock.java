// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;

public class ShulkerBoxBlock extends BaseEntityBlock {
    public static final net.minecraft.world.level.block.state.properties.EnumProperty<net.minecraft.core.Direction> FACING =
        net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;
    public static final net.minecraft.resources.Identifier CONTENTS = net.minecraft.resources.Identifier.withDefaultNamespace("contents");

    public net.minecraft.world.item.DyeColor getColor() {
        String path = blockKey().getPath();
        return path.equals("shulker_box") ? null
            : net.minecraft.world.item.DyeColor.byName(path.substring(0, path.length() - "_shulker_box".length()), null);
    }

    protected ShulkerBoxBlock(int index) {
        super(index);
    }

    @Override
    protected boolean hasInventory() {
        return true;
    }
}
