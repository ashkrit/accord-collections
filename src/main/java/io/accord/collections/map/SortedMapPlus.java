package io.accord.collections.map;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Concrete {@link MapPlus} backed by a {@link TreeMap}.
 * Only {@link Ordering#ASCENDING} and {@link Ordering#DESCENDING} are accepted.
 */
public class SortedMapPlus<K extends Comparable<K>, V> extends AbstractMapPlus<K, V> {

    public SortedMapPlus(Ordering ordering) {
        super(buildTree(ordering));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Map buildTree(Ordering ordering) {
        return switch (ordering) {
            case ASCENDING -> new TreeMap<>();
            case DESCENDING -> new TreeMap<>(Comparator.reverseOrder());
            case INSERTION -> throw new IllegalArgumentException(
                    "INSERTION ordering is not supported by SortedMapPlus; use StandardMapPlus instead");
        };
    }
}
