package io.accord.collections.map;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * An extension of {@link Map} with declared contracts: no null keys, no null values,
 * and live view operations.
 */
public interface MapPlus<K, V> extends Map<K, V> {

    /**
     * Returns a live view of this map with entries sorted by the given ordering.
     */
    MapPlus<K, V> view(Ordering ordering);

    /**
     * Returns a live view of this map with values projected by the given function.
     */
    <W> MapPlus<K, W> project(Function<? super V, ? extends W> projection);

    /**
     * Returns a live view of this map with only entries matching the given predicate.
     */
    MapPlus<K, V> filter(Predicate<? super V> predicate);

    /**
     * Returns a live view where this map's values are used as keys into {@code next},
     * yielding a map from this map's keys to {@code next}'s values.
     */
    <W> MapPlus<K, W> then(MapPlus<V, W> next);

    /**
     * Returns a live merged view of this map and {@code other} using the given strategy.
     */
    MapPlus<K, V> merge(MapPlus<K, V> other, MergeStrategy strategy);

    // --- Static factories ---

    static <K, V> MapPlus<K, V> empty() {
        return new StandardMapPlus<>();
    }

    @SafeVarargs
    static <K, V> MapPlus<K, V> of(Map.Entry<K, V>... entries) {
        StandardMapPlus<K, V> map = new StandardMapPlus<>();
        for (Map.Entry<K, V> e : entries) {
            map.put(e.getKey(), e.getValue());
        }
        return map;
    }
}
