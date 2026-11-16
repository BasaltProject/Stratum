// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.basaltmc.stratum.bukkit.facade.TileNbt;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class CampfireBlockEntity extends BlockEntity implements Clearable {
    public CampfireBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityType.named("campfire"), worldPosition, blockState);
    }

    public CampfireBlockEntity(ServerLevel level, BlockPos pos, CompoundTag nbt) {
        super(level, pos, nbt);
    }

    public boolean isLit() {
        return getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT);
    }

    public boolean placeFood(ServerLevel level, net.minecraft.world.entity.LivingEntity entity, net.minecraft.world.item.ItemStack food) {
        org.bukkit.inventory.ItemStack bukkitFood = org.bukkit.craftbukkit.inventory.CraftItemStack.asCraftMirror(food);
        if (!(level.getWorld().getBlockAt(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ()).getState()
                instanceof org.bukkit.block.Campfire campfire)) {
            return false;
        }
        int time = -1;
        java.util.Iterator<org.bukkit.inventory.Recipe> recipes = org.bukkit.Bukkit.recipeIterator();
        while (recipes.hasNext()) {
            if (recipes.next() instanceof org.bukkit.inventory.CampfireRecipe recipe && recipe.getInputChoice().test(bukkitFood)) {
                time = recipe.getCookingTime();
                break;
            }
        }
        if (time < 0) {
            return false;
        }
        for (int slot = 0; slot < campfire.getSize(); slot++) {
            org.bukkit.inventory.ItemStack present = campfire.getItem(slot);
            if (present == null || present.getType().isAir()) {
                campfire.setItem(slot, bukkitFood.asOne());
                campfire.setCookTimeTotal(slot, time);
                campfire.setCookTime(slot, 0);
                campfire.update();
                return true;
            }
        }
        return false;
    }
    public static void cookTick(ServerLevel level, BlockPos pos, BlockState state, CampfireBlockEntity entity, RecipeManager.CachedCheck<SingleRecipeInput, CampfireCookingRecipe> recipeCache) {
    }
    public static void cooldownTick(Level level, BlockPos pos, BlockState state, CampfireBlockEntity entity) {
    }
    public static void particleTick(Level level, BlockPos pos, BlockState state, CampfireBlockEntity entity) {
    }
    public NonNullList<ItemStack> getItems() {
        throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.level.block.entity.CampfireBlockEntity#getItems");
    }
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
    }
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
    }
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return super.getUpdateTag(registries);
    }
    public void clearContent() {
        net.minecraft.world.Clearable.super.clearContent();
    }
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
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
