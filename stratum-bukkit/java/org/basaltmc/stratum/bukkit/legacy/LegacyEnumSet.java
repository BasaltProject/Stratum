package org.basaltmc.stratum.bukkit.legacy;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

/** Stands in for java.util.EnumSet in plugins built when elements such as Sound and Biome were enums. */
public class LegacyEnumSet<E> extends AbstractSet<E> implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;

    private final Class<E> elementType;
    private final TreeSet<E> elements = new TreeSet<>(LegacyKeys.kOrder);

    private LegacyEnumSet(Class<E> elementType) {
        this.elementType = Objects.requireNonNull(elementType);
    }

    public static <E> LegacyEnumSet<E> noneOf(Class<E> elementType) {
        return new LegacyEnumSet<>(elementType);
    }

    public static <E> LegacyEnumSet<E> newEnumSet(Iterable<E> iterable, Class<E> elementType) {
        LegacyEnumSet<E> set = new LegacyEnumSet<>(elementType);
        for (E element : iterable) {
            set.add(element);
        }
        return set;
    }

    public static <E> LegacyEnumSet<E> allOf(Class<E> elementType) {
        LegacyEnumSet<E> set = new LegacyEnumSet<>(elementType);
        set.elements.addAll(LegacyKeys.universe(elementType));
        return set;
    }

    public static <E> LegacyEnumSet<E> copyOf(LegacyEnumSet<E> other) {
        return other.clone();
    }

    @SuppressWarnings("unchecked")
    public static <E> LegacyEnumSet<E> copyOf(Collection<E> other) {
        if (other instanceof LegacyEnumSet<E> set) {
            return set.clone();
        }
        if (other.isEmpty()) {
            throw new IllegalArgumentException("Collection is empty");
        }
        E first = other.iterator().next();
        Class<E> type = (Class<E>) (first instanceof Enum<?> value ? value.getDeclaringClass() : first.getClass());
        LegacyEnumSet<E> set = new LegacyEnumSet<>(type);
        set.addAll(other);
        return set;
    }

    public static <E> LegacyEnumSet<E> complementOf(LegacyEnumSet<E> other) {
        LegacyEnumSet<E> set = allOf(other.elementType);
        set.elements.removeAll(other.elements);
        return set;
    }

    @SuppressWarnings("unchecked")
    private static <E> LegacyEnumSet<E> with(E first, E... rest) {
        Class<E> type = (Class<E>) (first instanceof Enum<?> value ? value.getDeclaringClass() : first.getClass());
        LegacyEnumSet<E> set = new LegacyEnumSet<>(type);
        set.add(first);
        set.addAll(List.of(rest));
        return set;
    }

    @SuppressWarnings("unchecked")
    public static <E> LegacyEnumSet<E> of(E first) {
        return with(first);
    }

    @SuppressWarnings("unchecked")
    public static <E> LegacyEnumSet<E> of(E first, E second) {
        return with(first, second);
    }

    @SuppressWarnings("unchecked")
    public static <E> LegacyEnumSet<E> of(E first, E second, E third) {
        return with(first, second, third);
    }

    @SuppressWarnings("unchecked")
    public static <E> LegacyEnumSet<E> of(E first, E second, E third, E fourth) {
        return with(first, second, third, fourth);
    }

    @SuppressWarnings("unchecked")
    public static <E> LegacyEnumSet<E> of(E first, E second, E third, E fourth, E fifth) {
        return with(first, second, third, fourth, fifth);
    }

    @SafeVarargs
    public static <E> LegacyEnumSet<E> of(E first, E... rest) {
        return with(first, rest);
    }

    public static <E> LegacyEnumSet<E> range(E from, E to) {
        if (LegacyKeys.kOrder.compare(from, to) > 0) {
            throw new IllegalArgumentException(from + " > " + to);
        }
        @SuppressWarnings("unchecked")
        Class<E> type = (Class<E>) (from instanceof Enum<?> value ? value.getDeclaringClass() : from.getClass());
        LegacyEnumSet<E> set = new LegacyEnumSet<>(type);
        for (E each : LegacyKeys.universe(type)) {
            if (LegacyKeys.kOrder.compare(each, from) >= 0 && LegacyKeys.kOrder.compare(each, to) <= 0) {
                set.add(each);
            }
        }
        return set;
    }

    @Override
    public boolean add(E element) {
        return elements.add(Objects.requireNonNull(element));
    }

    @Override
    public boolean remove(Object element) {
        return element != null && elements.remove(element);
    }

    @Override
    public boolean contains(Object element) {
        return element != null && elements.contains(element);
    }

    @Override
    public Iterator<E> iterator() {
        return elements.iterator();
    }

    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public void clear() {
        elements.clear();
    }

    @Override
    public LegacyEnumSet<E> clone() {
        LegacyEnumSet<E> copy = new LegacyEnumSet<>(elementType);
        copy.elements.addAll(elements);
        return copy;
    }
}
