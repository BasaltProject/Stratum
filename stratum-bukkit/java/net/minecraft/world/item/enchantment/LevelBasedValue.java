// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.item.enchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Registry;

public interface LevelBasedValue {
    static MapCodec<? extends LevelBasedValue> bootstrap(Registry<MapCodec<? extends LevelBasedValue>> registry) {
        return null;
    }

    static Constant constant(float value) {
        return new Constant(value);
    }

    static Linear perLevel(float base, float perLevelAboveFirst) {
        return new Linear(base, perLevelAboveFirst);
    }

    static Linear perLevel(float perLevel) {
        return new Linear(perLevel, perLevel);
    }

    static Lookup lookup(List<Float> values, LevelBasedValue fallback) {
        return new Lookup(values, fallback);
    }

    float calculate(int level);

    default void encode(List<Double> out, int level) {
        out.add(0.0);
        out.add((double) calculate(level));
    }

    static float run(LevelBasedValue value, int level) {
        List<Double> program = new ArrayList<>();
        value.encode(program, level);
        double[] flat = new double[program.size()];
        for (int at = 0; at < flat.length; at++) {
            flat[at] = program.get(at);
        }
        return org.basaltmc.stratum.bukkit.MechanicsNatives.levelValue(flat, level);
    }

    /** Reads the datapack form: a bare number or an object with a `type`. */
    static LevelBasedValue fromData(Object raw) {
        if (raw instanceof Number number) {
            return new Constant(number.floatValue());
        }
        if (!(raw instanceof Map<?, ?> map)) {
            return new Constant(0.0F);
        }
        String type = String.valueOf(map.get("type"));
        switch (type.startsWith("minecraft:") ? type.substring(10) : type) {
            case "linear":
                return new Linear(number(map.get("base")), number(map.get("per_level_above_first")));
            case "levels_squared":
                return new LevelsSquared(number(map.get("added")));
            case "clamped":
                return new Clamped(fromData(map.get("value")), number(map.get("min")), number(map.get("max")));
            case "fraction":
                return new Fraction(fromData(map.get("numerator")), fromData(map.get("denominator")));
            case "exponent":
                return new Exponent(fromData(map.get("base")), fromData(map.get("power")));
            case "lookup":
                List<Float> values = new ArrayList<>();
                if (map.get("values") instanceof List<?> list) {
                    for (Object entry : list) {
                        values.add(number(entry));
                    }
                }
                return new Lookup(values, fromData(map.get("fallback")));
            default:
                return new Constant(number(map.get("value")));
        }
    }

    private static float number(Object value) {
        return value instanceof Number number ? number.floatValue() : 0.0F;
    }

    public static record Constant(float value) implements LevelBasedValue {
        public static final Codec<Constant> CODEC = Codec.FLOAT.xmap(Constant::new, Constant::value);
        public static final MapCodec<Constant> TYPED_CODEC = org.basaltmc.stratum.bukkit.facade.LazyCodecs.mapCodec(net.minecraft.world.item.enchantment.LevelBasedValue.Constant.class, "TYPED_CODEC");

        @Override
        public void encode(List<Double> out, int level) {
            out.add(0.0);
            out.add((double) value);
        }

        @Override
        public float calculate(int level) {
            return value;
        }
            public MapCodec<Constant> codec() {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.item.enchantment.LevelBasedValue.Constant#codec");
        }
}

    public static record Lookup(List<Float> values, LevelBasedValue fallback) implements LevelBasedValue {
        public static final MapCodec<Lookup> CODEC = org.basaltmc.stratum.bukkit.facade.LazyCodecs.mapCodec(net.minecraft.world.item.enchantment.LevelBasedValue.Lookup.class, "CODEC");

        @Override
        public void encode(List<Double> out, int level) {
            out.add(6.0);
            out.add((double) values.size());
            for (Float entry : values) {
                out.add((double) entry);
            }
            fallback.encode(out, level);
        }

        @Override
        public float calculate(int level) {
            return run(this, level);
        }
            public MapCodec<Lookup> codec() {
            return CODEC;
        }
}

    public static record Linear(float base, float perLevelAboveFirst) implements LevelBasedValue {
        public static final MapCodec<Linear> CODEC = org.basaltmc.stratum.bukkit.facade.LazyCodecs.mapCodec(net.minecraft.world.item.enchantment.LevelBasedValue.Linear.class, "CODEC");

        @Override
        public void encode(List<Double> out, int level) {
            out.add(1.0);
            out.add((double) base);
            out.add((double) perLevelAboveFirst);
        }

        @Override
        public float calculate(int level) {
            return run(this, level);
        }
            public MapCodec<Linear> codec() {
            return CODEC;
        }
}

    public static record Clamped(LevelBasedValue value, float min, float max) implements LevelBasedValue {
        public static final MapCodec<Clamped> CODEC = org.basaltmc.stratum.bukkit.facade.LazyCodecs.mapCodec(net.minecraft.world.item.enchantment.LevelBasedValue.Clamped.class, "CODEC");

        @Override
        public void encode(List<Double> out, int level) {
            out.add(3.0);
            out.add((double) min);
            out.add((double) max);
            value.encode(out, level);
        }

        @Override
        public float calculate(int level) {
            return run(this, level);
        }
            public MapCodec<Clamped> codec() {
            return CODEC;
        }
}

    public static record Fraction(LevelBasedValue numerator, LevelBasedValue denominator) implements LevelBasedValue {
        public static final MapCodec<Fraction> CODEC = org.basaltmc.stratum.bukkit.facade.LazyCodecs.mapCodec(net.minecraft.world.item.enchantment.LevelBasedValue.Fraction.class, "CODEC");

        @Override
        public void encode(List<Double> out, int level) {
            out.add(4.0);
            numerator.encode(out, level);
            denominator.encode(out, level);
        }

        @Override
        public float calculate(int level) {
            return run(this, level);
        }
            public MapCodec<Fraction> codec() {
            return CODEC;
        }
}

    public static record Exponent(LevelBasedValue base, LevelBasedValue power) implements LevelBasedValue {
        public static final MapCodec<Exponent> CODEC = org.basaltmc.stratum.bukkit.facade.LazyCodecs.mapCodec(net.minecraft.world.item.enchantment.LevelBasedValue.Exponent.class, "CODEC");

        @Override
        public void encode(List<Double> out, int level) {
            out.add(5.0);
            base.encode(out, level);
            power.encode(out, level);
        }

        @Override
        public float calculate(int level) {
            return run(this, level);
        }
            public MapCodec<Exponent> codec() {
            return CODEC;
        }
}

    public static record LevelsSquared(float added) implements LevelBasedValue {
        public static final MapCodec<LevelsSquared> CODEC = org.basaltmc.stratum.bukkit.facade.LazyCodecs.mapCodec(net.minecraft.world.item.enchantment.LevelBasedValue.LevelsSquared.class, "CODEC");

        @Override
        public void encode(List<Double> out, int level) {
            out.add(2.0);
            out.add((double) added);
        }

        @Override
        public float calculate(int level) {
            return run(this, level);
        }
            public MapCodec<LevelsSquared> codec() {
            return CODEC;
        }
}
    Codec<LevelBasedValue> DISPATCH_CODEC = org.basaltmc.stratum.bukkit.facade.LazyCodecs.codec(net.minecraft.world.item.enchantment.LevelBasedValue.class, "DISPATCH_CODEC");
    Codec<LevelBasedValue> CODEC = org.basaltmc.stratum.bukkit.facade.LazyCodecs.codec(net.minecraft.world.item.enchantment.LevelBasedValue.class, "CODEC");
    default MapCodec<? extends LevelBasedValue> codec() {
        throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.item.enchantment.LevelBasedValue#codec");
    }
}
