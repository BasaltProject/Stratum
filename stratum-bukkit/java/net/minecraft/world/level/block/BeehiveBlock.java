// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;

public class BeehiveBlock extends BaseEntityBlock {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final net.minecraft.world.level.block.state.properties.IntegerProperty HONEY_LEVEL = net.minecraft.world.level.block.state.properties.BlockStateProperties.HONEY_LEVEL;
    public static final int MAX_HONEY_LEVELS = 5;
    public void resetHoneyLevel(net.minecraft.world.level.Level level, net.minecraft.world.level.block.state.BlockState state,
            net.minecraft.core.BlockPos pos) {
        level.setBlockAndUpdate(pos, state.setValue(HONEY_LEVEL, 0));
    }

    public void releaseBeesAndResetHoneyLevel(net.minecraft.world.level.Level level,
            net.minecraft.world.level.block.state.BlockState state, net.minecraft.core.BlockPos pos,
            net.minecraft.world.entity.player.Player player,
            net.minecraft.world.level.block.entity.BeehiveBlockEntity.BeeReleaseStatus beeReleaseStatus) {
        resetHoneyLevel(level, state, pos);
        if (level.getBlockEntity(pos) instanceof net.minecraft.world.level.block.entity.BeehiveBlockEntity hive) {
            hive.emptyAllLivingFromHive(player, state, beeReleaseStatus);
        }
    }

    protected BeehiveBlock(int index) {
        super(index);
    }
}
