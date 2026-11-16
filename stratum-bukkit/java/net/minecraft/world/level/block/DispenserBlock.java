// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block;
import net.minecraft.core.Position;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;

import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.world.phys.Vec3;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
public class DispenserBlock extends BaseEntityBlock {
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

    public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;

    public static Position getDispensePosition(net.minecraft.core.dispenser.BlockSource source, double offset, net.minecraft.world.phys.Vec3 shift) {
        Direction face = source.state().getValue(FACING);
        net.minecraft.world.phys.Vec3 center = source.center();
        return center.add(offset * face.getStepX() + shift.x, offset * face.getStepY() + shift.y, offset * face.getStepZ() + shift.z);
    }

    protected DispenserBlock(int index) {
        super(index);
    }

    @Override
    protected boolean hasInventory() {
        return true;
    }

    protected void dispenseFrom(net.minecraft.server.level.ServerLevel level, net.minecraft.world.level.block.state.BlockState state, net.minecraft.core.BlockPos pos) {
        if (level.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()).getState() instanceof org.bukkit.block.Dispenser dispenser) {
            dispenser.dispense();
        } else if (level.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()).getState() instanceof org.bukkit.block.Dropper dropper) {
            dropper.drop();
        }
    }

    public static Position getDispensePosition(BlockSource source) {
        return getDispensePosition(source, 0.7, Vec3.ZERO);
    }
    public static final Map<Item, DispenseItemBehavior> DISPENSER_REGISTRY = null;
    public static void registerBehavior(ItemLike item, DispenseItemBehavior behavior) {
    }
    public static void registerProjectileBehavior(ItemLike item) {
    }
    protected DispenserBlock(BlockBehaviour.Properties properties) {
        super((int) 0);
    }
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }
    protected DispenseItemBehavior getDispenseMethod(Level level, ItemStack itemStack) {
        throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.level.block.DispenserBlock#getDispenseMethod");
    }
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, Orientation orientation, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, block, orientation, movedByPiston);
    }
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
    }
    public BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return super.newBlockEntity(worldPosition, blockState);
    }
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context);
    }
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return super.hasAnalogOutputSignal(state);
    }
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        return super.getAnalogOutputSignal(state, level, pos, direction);
    }
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return super.rotate(state, rotation);
    }
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return super.mirror(state, mirror);
    }
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }
}
