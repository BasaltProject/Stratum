// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world;

import com.mojang.serialization.Codec;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.basaltmc.stratum.bukkit.WorldEnvNatives;
import org.basaltmc.stratum.bukkit.facade.NativeSequenceRandom;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

/** The random sequences of the main world; the streams and their defaults live in the native world. */
public class RandomSequences extends SavedData {
    public static final Codec<RandomSequences> CODEC = org.basaltmc.stratum.bukkit.facade.LazyCodecs.codec(net.minecraft.world.RandomSequences.class, "CODEC");
    public static final SavedDataType<RandomSequences> TYPE = new SavedDataType<RandomSequences>(Identifier.withDefaultNamespace("random_sequences"), RandomSequences::new, CODEC, net.minecraft.util.datafix.DataFixTypes.SAVED_DATA_RANDOM_SEQUENCES);

    public RandomSequences() {
    }

    private RandomSequences(int salt, boolean includeWorldSeed, boolean includeSequenceId, Map<Identifier, RandomSequence> sequences) {
    }

    private static String world() {
        return org.bukkit.Bukkit.getWorlds().get(0).getName();
    }

    public RandomSource get(Identifier key, long worldSeed) {
        return new NativeSequenceRandom(world(), key.toString());
    }

    public void forAllSequences(BiConsumer<Identifier, RandomSequence> consumer) {
    }

    public void setSeedDefaults(int salt, boolean includeWorldSeed, boolean includeSequenceId) {
    }

    public int clear() {
        return (int) WorldEnvNatives.randomSequence(world(), "minecraft:none", 8, 0);
    }

    public void reset(Identifier id, long worldSeed) {
        WorldEnvNatives.randomSequence(world(), id.toString(), 7, 0);
    }

    public void reset(Identifier id, long worldSeed, int salt, boolean includeWorldSeed, boolean includeSequenceId) {
        reset(id, worldSeed);
    }
    class DirtyMarkingRandomSource implements RandomSource {
        private DirtyMarkingRandomSource(RandomSource random) {
            
        }
        public RandomSource fork() {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.RandomSequences.DirtyMarkingRandomSource#fork");
        }
        public PositionalRandomFactory forkPositional() {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.RandomSequences.DirtyMarkingRandomSource#forkPositional");
        }
        public void setSeed(long seed) {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.RandomSequences.DirtyMarkingRandomSource#setSeed");
        }
        public int nextInt() {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.RandomSequences.DirtyMarkingRandomSource#nextInt");
        }
        public int nextInt(int bound) {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.RandomSequences.DirtyMarkingRandomSource#nextInt");
        }
        public long nextLong() {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.RandomSequences.DirtyMarkingRandomSource#nextLong");
        }
        public boolean nextBoolean() {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.RandomSequences.DirtyMarkingRandomSource#nextBoolean");
        }
        public float nextFloat() {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.RandomSequences.DirtyMarkingRandomSource#nextFloat");
        }
        public double nextDouble() {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.RandomSequences.DirtyMarkingRandomSource#nextDouble");
        }
        public double nextGaussian() {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.RandomSequences.DirtyMarkingRandomSource#nextGaussian");
        }
        public boolean equals(Object obj) {
            return super.equals(obj);
        }
        public int hashCode() {
            return super.hashCode();
        }
    }
}
