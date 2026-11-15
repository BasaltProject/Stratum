// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.CreakingHeartState;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;

public class CreakingHeartBlock extends BaseEntityBlock {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    public static final EnumProperty<CreakingHeartState> STATE = BlockStateProperties.CREAKING_HEART_STATE;
    public static final net.minecraft.world.level.block.state.properties.BooleanProperty NATURAL = net.minecraft.world.level.block.state.properties.BlockStateProperties.NATURAL;
    protected CreakingHeartBlock(int index) {
        super(index);
    }
}
