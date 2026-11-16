package org.basaltmc.stratum.bukkit.legacy;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/** Stands in for java.util.EnumMap in plugins built when keys such as Sound and Biome were enums. */
public class LegacyEnumMap<K, V> extends AbstractMap<K, V> implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;

    private final Class<K> keyType;
    private TreeMap<K, V> entries;

    public LegacyEnumMap(Class<K> keyType) {
        this.keyType = Objects.requireNonNull(keyType);
        this.entries = new TreeMap<>(LegacyKeys.kOrder);
    }

    public static <K, V> LegacyEnumMap<K, V> newEnumMap(Class<K> keyType) {
        return new LegacyEnumMap<>(keyType);
    }

    public static <K, V> LegacyEnumMap<K, V> newEnumMap(Map<K, ? extends V> other) {
        return new LegacyEnumMap<>(other);
    }

    public LegacyEnumMap(LegacyEnumMap<K, ? extends V> other) {
        this(other.keyType);
        entries.putAll(other.entries);
    }

    public LegacyEnumMap(Map<K, ? extends V> other) {
        this(other instanceof LegacyEnumMap<K, ? extends V> map ? map.keyType : keyTypeOf(other));
        entries.putAll(other);
    }

    @SuppressWarnings("unchecked")
    private static <K> Class<K> keyTypeOf(Map<K, ?> map) {
        if (map.isEmpty()) {
            throw new IllegalArgumentException("Specified map is empty");
        }
        Object first = map.keySet().iterator().next();
        Class<?> type = first.getClass();
        return (Class<K>) (type.isEnum() ? type : type.getEnclosingClass() != null && type.getEnclosingClass().isEnum()
            ? type.getEnclosingClass() : type);
    }

    @Override
    public V put(K key, V value) {
        return entries.put(Objects.requireNonNull(key), value);
    }

    @Override
    public V get(Object key) {
        return key == null ? null : entries.get(key);
    }

    @Override
    public boolean containsKey(Object key) {
        return key != null && entries.containsKey(key);
    }

    @Override
    public V remove(Object key) {
        return key == null ? null : entries.remove(key);
    }

    @Override
    public void clear() {
        entries.clear();
    }

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return entries.entrySet();
    }

    @Override
    public LegacyEnumMap<K, V> clone() {
        return new LegacyEnumMap<>(this);
    }
}
