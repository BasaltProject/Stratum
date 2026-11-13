// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.entity.animal;
import net.minecraft.resources.Identifier;
public interface TemperatureVariants {
    Identifier TEMPERATE = Identifier.withDefaultNamespace("temperate");
    Identifier WARM = Identifier.withDefaultNamespace("warm");
    Identifier COLD = Identifier.withDefaultNamespace("cold");
}
