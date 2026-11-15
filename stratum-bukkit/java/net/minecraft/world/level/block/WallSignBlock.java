// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;

public class WallSignBlock extends SignBlock {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    protected WallSignBlock(int index) {
        super(index);
    }
}
