// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.levelgen.feature;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
public record NoOpFeature() implements Feature {
    public static final MapCodec<NoOpFeature> CODEC = org.basaltmc.stratum.bukkit.facade.LazyCodecs.mapCodec(net.minecraft.world.level.levelgen.feature.NoOpFeature.class, "CODEC");
    public MapCodec<NoOpFeature> codec() {
        return CODEC;
    }
    public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {
        return true;
    }
}
