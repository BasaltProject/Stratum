// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.entity.decoration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
public class GlowItemFrame extends ItemFrame {
    public GlowItemFrame(EntityType<? extends ItemFrame> type, Level level) {
        super(type, level);
    }
    public GlowItemFrame(Level level, BlockPos pos, Direction direction) {
        super(level, pos, direction);
    }
    public SoundEvent getRemoveItemSound() {
        return net.minecraft.sounds.SoundEvents.GLOW_ITEM_FRAME_REMOVE_ITEM;
    }
    public SoundEvent getBreakSound() {
        return net.minecraft.sounds.SoundEvents.GLOW_ITEM_FRAME_BREAK;
    }
    public SoundEvent getPlaceSound() {
        return net.minecraft.sounds.SoundEvents.GLOW_ITEM_FRAME_PLACE;
    }
    public SoundEvent getAddItemSound() {
        return net.minecraft.sounds.SoundEvents.GLOW_ITEM_FRAME_ADD_ITEM;
    }
    public SoundEvent getRotateItemSound() {
        return net.minecraft.sounds.SoundEvents.GLOW_ITEM_FRAME_ROTATE_ITEM;
    }
    protected ItemStack getFrameItemStack() {
        return new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.GLOW_ITEM_FRAME);
    }
}
