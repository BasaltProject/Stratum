// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.attribute.BedRule;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BedBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;
    public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;

    protected BedBlock(int index) {
        super(index);
    }

    public DyeColor getColor() {
        String path = blockKey().getPath();
        String name = path.substring(0, path.length() - "_bed".length());
        for (DyeColor color : DyeColor.values()) {
            if (color.getName().equals(name)) {
                return color;
            }
        }
        return DyeColor.WHITE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return org.basaltmc.stratum.bukkit.facade.BlockEntityTypes.detached(blockKey(), pos);
    }
    public BedBlock(DyeColor color, BlockBehaviour.Properties properties) {
        super((int) 0);
    }
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return super.getShape(state, level, pos, context);
    }
    protected EnvironmentAttribute<BedRule> getBedEnvironmentAttribute() {
        throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.level.block.BedBlock#getBedEnvironmentAttribute");
    }
    protected InteractionResult destroyOnUse(BlockState state, Level level, BlockPos pos, Player player) {
        throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.level.block.BedBlock#destroyOnUse");
    }
    protected void destroyOnLeave(Level level, BlockPos pos) {
    }
}
