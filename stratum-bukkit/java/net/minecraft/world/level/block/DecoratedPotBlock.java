// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;

public class DecoratedPotBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final net.minecraft.resources.Identifier SHERDS_DYNAMIC_DROP_ID = net.minecraft.resources.Identifier.withDefaultNamespace("sherds");
    public static final net.minecraft.world.level.block.state.properties.EnumProperty<net.minecraft.core.Direction> HORIZONTAL_FACING = net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;
    public static final net.minecraft.world.level.block.state.properties.BooleanProperty CRACKED = net.minecraft.world.level.block.state.properties.BlockStateProperties.CRACKED;
    public static final net.minecraft.world.level.block.state.properties.BooleanProperty WATERLOGGED = net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;
    protected DecoratedPotBlock(int index) {
        super(index);
    }
}
