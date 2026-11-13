// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.storage.loot.providers.number.floats;
import net.minecraft.util.Mth;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.UnaryProvider;
public record Round(Holder<ContextFloatProvider> input) implements ContextFloatProvider, UnaryProvider<ContextFloatProvider> {
    public static final MapCodec<Round> MAP_CODEC = org.basaltmc.stratum.bukkit.facade.LazyCodecs.mapCodec(net.minecraft.world.level.storage.loot.providers.number.floats.Round.class, "MAP_CODEC");
    public MapCodec<Round> codec() {
        return (com.mojang.serialization.MapCodec) MAP_CODEC;
    }
    public float getFloatUnsafe(LootContext context) {
        float v = input().value().getFloatUnsafe(context);
        return (float) Math.round(v);

    }
}
