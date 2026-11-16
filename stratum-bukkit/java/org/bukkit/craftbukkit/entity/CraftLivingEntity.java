package org.bukkit.craftbukkit.entity;
public class CraftLivingEntity extends CraftEntity {
    protected CraftLivingEntity() {
    }

    @Override
    public net.minecraft.world.entity.LivingEntity getHandle() {
        return (net.minecraft.world.entity.LivingEntity) super.getHandle();
    }
}
