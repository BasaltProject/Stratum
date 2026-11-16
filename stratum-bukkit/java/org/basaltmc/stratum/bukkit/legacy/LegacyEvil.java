package org.basaltmc.stratum.bukkit.legacy;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

/** The numeric-id methods old plugins call on blocks, worlds and stacks, answered from the flattening table. */
public final class LegacyEvil {
    private LegacyEvil() {}

    private static BlockData state(int id, byte data) {
        Material legacy = LegacyMaterials.getMaterial(id);
        return legacy == null ? Material.AIR.createBlockData() : LegacyMaterials.blockData(legacy, data);
    }

    public static int getTypeId(Block block) {
        return LegacyBlocks.blockOf(block.getBlockData());
    }

    public static int getTypeId(BlockState state) {
        return LegacyBlocks.blockOf(state.getBlockData());
    }

    public static boolean setTypeId(Block block, int id) {
        return setTypeIdAndData(block, id, (byte) 0, true);
    }

    public static boolean setTypeId(Block block, int id, boolean applyPhysics) {
        return setTypeIdAndData(block, id, (byte) 0, applyPhysics);
    }

    public static boolean setTypeIdAndData(Block block, int id, byte data, boolean applyPhysics) {
        block.setBlockData(state(id, data), applyPhysics);
        return true;
    }

    public static void setData(Block block, byte data) {
        setData(block, data, true);
    }

    public static void setData(Block block, byte data, boolean applyPhysics) {
        block.setBlockData(state(getTypeId(block), data), applyPhysics);
    }

    public static boolean setTypeId(BlockState state, int id) {
        state.setBlockData(state(id, (byte) 0));
        return true;
    }

    public static int getBlockTypeIdAt(World world, int x, int y, int z) {
        return getTypeId(world.getBlockAt(x, y, z));
    }

    public static int getBlockTypeIdAt(World world, Location location) {
        return getTypeId(location.getBlock());
    }

    public static int getTypeId(ItemStack stack) {
        return LegacyMaterials.toLegacy(stack.getType()).getId();
    }

    public static void setTypeId(ItemStack stack, int id) {
        Material legacy = LegacyMaterials.getMaterial(id);
        stack.setType(legacy == null ? Material.AIR : org.bukkit.Bukkit.getUnsafe().fromLegacy(
            new org.bukkit.material.MaterialData(legacy, (byte) 0)));
    }
}
