// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block.entity;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.basaltmc.stratum.bukkit.facade.TileNbt;

public class CommandBlockEntity extends BlockEntity {
    public CommandBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityType.named("command_block"), worldPosition, blockState);
    }

    public CommandBlockEntity(ServerLevel level, BlockPos pos, CompoundTag nbt) {
        super(level, pos, nbt);
    }

    public String getCommand() {
        return nbt().getStringOr("Command", "");
    }

    public void setCommand(String command) {
        TileNbt.update(this, tag -> tag.putString("Command", command));
    }

    public boolean isPowered() {
        return nbt().getBooleanOr("powered", false);
    }

    public boolean isAutomatic() {
        return nbt().getBooleanOr("auto", false);
    }

    public boolean isConditionMet() {
        return nbt().getBooleanOr("conditionMet", false);
    }

    public int getSuccessCount() {
        return nbt().getIntOr("SuccessCount", 0);
    }

    public static enum Mode {
        SEQUENCE, AUTO, REDSTONE;
    }

    public void setPowered(boolean powered) {
        TileNbt.update(this, tag -> tag.putBoolean("powered", powered));
    }

    public void setAutomatic(boolean auto) {
        TileNbt.update(this, tag -> tag.putBoolean("auto", auto));
    }

    public boolean wasConditionMet() {
        return isConditionMet();
    }

    public Mode getMode() {
        String path = getBlockState().getBlock().blockKey().getPath();
        return path.equals("repeating_command_block") ? Mode.AUTO : path.equals("chain_command_block") ? Mode.SEQUENCE : Mode.REDSTONE;
    }

    public boolean isConditional() {
        BlockState state = getBlockState();
        return state.getBlock() instanceof net.minecraft.world.level.block.CommandBlock && state.getValue(net.minecraft.world.level.block.CommandBlock.CONDITIONAL);
    }

    public boolean markConditionMet() {
        boolean met = true;
        if (isConditional()) {
            BlockPos behind = getBlockPos().relative(getBlockState().getValue(net.minecraft.world.level.block.CommandBlock.FACING).getOpposite());
            met = getLevel().getBlockState(behind).getBlock() instanceof net.minecraft.world.level.block.CommandBlock
                && getLevel().getBlockEntity(behind) instanceof CommandBlockEntity other && other.getSuccessCount() > 0;
        }
        boolean result = met;
        TileNbt.update(this, tag -> tag.putBoolean("conditionMet", result));
        return result;
    }
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
    }
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
    }
    public BaseCommandBlock getCommandBlock() {
        throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.level.block.entity.CommandBlockEntity#getCommandBlock");
    }
    public void onModeSwitch() {
    }
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);
    }
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
    }
    public void removeComponentsFromTag(ValueOutput output) {
        super.removeComponentsFromTag(output);
    }
}
