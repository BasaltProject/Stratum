package org.bukkit.craftbukkit.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import org.bukkit.entity.EntityType;

public final class CraftEntityType {
    private CraftEntityType() {
    }

    public static net.minecraft.world.entity.EntityType<?> bukkitToMinecraft(EntityType type) {
        return BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.parse(type.key().asString()));
    }

    public static EntityType minecraftToBukkit(net.minecraft.world.entity.EntityType<?> type) {
        return org.bukkit.Registry.ENTITY_TYPE.get(
            org.bukkit.NamespacedKey.fromString(BuiltInRegistries.ENTITY_TYPE.getKey(type).toString()));
    }
}
