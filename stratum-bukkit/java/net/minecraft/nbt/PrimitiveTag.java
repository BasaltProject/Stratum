// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.nbt;
public interface PrimitiveTag extends Tag {
    @Override
    default Tag copy() {
        return this;
    }
}
