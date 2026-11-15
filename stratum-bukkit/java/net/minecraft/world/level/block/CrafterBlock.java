// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;

public class CrafterBlock extends BaseEntityBlock {
    public static final BooleanProperty CRAFTING = BlockStateProperties.CRAFTING;
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
    protected CrafterBlock(int index) {
        super(index);
    }

    @Override
    protected boolean hasInventory() {
        return true;
    }
}
