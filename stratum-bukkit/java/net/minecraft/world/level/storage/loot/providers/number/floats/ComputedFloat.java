// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.storage.loot.providers.number.floats;

import com.mojang.serialization.MapCodec;
import java.util.function.ToDoubleFunction;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;

final class ComputedFloat implements ContextFloatProvider {
    private final ToDoubleFunction<LootContext> body;

    ComputedFloat(ToDoubleFunction<LootContext> body) {
        this.body = body;
    }

    public float getFloatUnsafe(LootContext context) {
        return (float) body.applyAsDouble(context);
    }

    public MapCodec<? extends ContextFloatProvider> codec() {
        throw new UnsupportedOperationException("computed providers are not serialised");
    }

    public void validate(ValidationContext context) {
    }
}
