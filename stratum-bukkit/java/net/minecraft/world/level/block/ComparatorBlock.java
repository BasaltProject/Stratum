// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ComparatorMode;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.basaltmc.stratum.bukkit.WorldNatives;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

public class ComparatorBlock extends DiodeBlock implements EntityBlock {
    public static final EnumProperty<ComparatorMode> MODE = BlockStateProperties.COMPARATOR_MODE;

    protected ComparatorBlock(int index) {
        super(index);
    }

    @Override
    protected int getDelay(BlockState state) {
        return 2;
    }

    @Override
    protected int getOutputSignal(BlockGetter level, BlockPos pos, BlockState state) {
        if (!(level instanceof net.minecraft.server.level.ServerLevel server)) {
            return 0;
        }
        byte[] raw = WorldNatives.blockEntityNbt(server.getWorld().getName(), pos.getX(), pos.getY(), pos.getZ());
        net.minecraft.nbt.CompoundTag tag = raw == null ? null : org.basaltmc.stratum.bukkit.facade.NbtCodec.fromBytes(raw);
        return tag == null ? 0 : tag.getIntOr("OutputSignal", 0);
    }
    public ComparatorBlock(BlockBehaviour.Properties properties) {
        super((int) 0);
    }
    public BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
    }
    protected boolean shouldTurnOn(Level level, BlockPos pos, BlockState state) {
        return super.shouldTurnOn(level, pos, state);
    }
    protected int getInputSignal(Level level, BlockPos pos, BlockState state) {
        return super.getInputSignal(level, pos, state);
    }
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }
    protected void checkTickOnNeighbor(Level level, BlockPos pos, BlockState state) {
        super.checkTickOnNeighbor(level, pos, state);
    }
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
    }
    protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int b0, int b1) {
        return super.triggerEvent(state, level, pos, b0, b1);
    }
    public BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.level.block.ComparatorBlock#newBlockEntity");
    }
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }
}
