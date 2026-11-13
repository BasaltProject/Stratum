// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.entity;

@FunctionalInterface
public interface EntityProcessor {
    EntityProcessor NOP = input -> input;

    Entity process(Entity input);
}
