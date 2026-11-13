// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.levelgen.feature;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
public record WeightedPlacedFeature(Holder<PlacedFeature> feature, float chance) {
    public static final Codec<WeightedPlacedFeature> CODEC = org.basaltmc.stratum.bukkit.facade.LazyCodecs.codec(net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature.class, "CODEC");
    public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {
        return feature.value().place(level, chunkGenerator, random, origin);
    }
}
