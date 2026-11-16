// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;

public abstract class AbstractBannerBlock extends BaseEntityBlock {
    protected AbstractBannerBlock(int index) {
        super(index);
    }

    public boolean isPossibleToRespawnInThis(BlockState state) {
        return true;
    }

    public DyeColor getColor() {
        String path = blockKey().getPath();
        return DyeColor.byName(path.substring(0, path.indexOf("_banner") >= 0 ? path.indexOf("_banner") : path.length())
            .replace("_wall", ""), DyeColor.WHITE);
    }
    protected AbstractBannerBlock(DyeColor color, BlockBehaviour.Properties properties) {
        super((int) 0);
    }
    public BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return super.newBlockEntity(worldPosition, blockState);
    }
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        return super.getCloneItemStack(level, pos, state, includeData);
    }
}
