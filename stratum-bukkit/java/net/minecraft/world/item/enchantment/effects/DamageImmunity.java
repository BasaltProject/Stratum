// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.item.enchantment.effects;
import com.mojang.serialization.Codec;
public record DamageImmunity() {
    public static final DamageImmunity INSTANCE = new DamageImmunity();
    public static final Codec<DamageImmunity> CODEC = org.basaltmc.stratum.bukkit.facade.LazyCodecs.codec(net.minecraft.world.item.enchantment.effects.DamageImmunity.class, "CODEC");
}
