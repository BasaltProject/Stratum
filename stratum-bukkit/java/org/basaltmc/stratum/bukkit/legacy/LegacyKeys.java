package org.basaltmc.stratum.bukkit.legacy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.bukkit.Keyed;
import org.bukkit.Registry;

/** Orders and enumerates the keys of an EnumMap or EnumSet, for types that were enums and are registry values now. */
final class LegacyKeys {
    static final Comparator<Object> kOrder = new Order();

    private static final class Order implements Comparator<Object>, Serializable {
        @Override
        public int compare(Object a, Object b) {
            if (a instanceof Enum<?> first && b instanceof Enum<?> second) {
                return Integer.compare(first.ordinal(), second.ordinal());
            }
            if (a instanceof Keyed first && b instanceof Keyed second) {
                return first.key().asString().compareTo(second.key().asString());
            }
            return String.valueOf(a).compareTo(String.valueOf(b));
        }
    }

    private LegacyKeys() {}

    static <K> List<K> universe(Class<K> type) {
        List<K> out = new ArrayList<>();
        if (type.isEnum()) {
            out.addAll(List.of(type.getEnumConstants()));
            return out;
        }
        for (java.lang.reflect.Field field : Registry.class.getFields()) {
            if (!java.lang.reflect.Modifier.isStatic(field.getModifiers())
                    || !Registry.class.isAssignableFrom(field.getType())) {
                continue;
            }
            try {
                Registry<?> registry = (Registry<?>) field.get(null);
                for (Object each : registry) {
                    if (type.isInstance(each)) {
                        out.add(type.cast(each));
                    }
                }
                if (!out.isEmpty()) {
                    out.sort(kOrder);
                    return out;
                }
            } catch (ReflectiveOperationException | RuntimeException e) {
                continue;
            }
        }
        throw new UnsupportedOperationException(type.getName() + " has no list of constants in this version");
    }
}
