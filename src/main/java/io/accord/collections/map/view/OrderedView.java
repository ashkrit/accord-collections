package io.accord.collections.map.view;

import io.accord.collections.map.AbstractMapPlus;
import io.accord.collections.map.MapPlus;
import io.accord.collections.map.Ordering;

import java.util.*;

/**
 * Live sorted/ordered view. entrySet() returns a sorted snapshot at iteration time;
 * get() delegates directly to the source.
 */
public class OrderedView<K, V> extends AbstractMapPlus<K, V> {

    private final MapPlus<K, V> source;
    private final Ordering ordering;

    public OrderedView(MapPlus<K, V> source, Ordering ordering) {
        super(new LiveAdapter<>(source, ordering));
        this.source = source;
        this.ordering = ordering;
    }

    @Override
    public V get(Object key) {
        if (key == null) return null;
        return source.get(key);
    }

    @Override
    public V put(K key, V value) {
        throw new UnsupportedOperationException("View is read-only");
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException("View is read-only");
    }

    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException("View is read-only");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("View is read-only");
    }

    private static class LiveAdapter<K, V> extends AbstractMap<K, V> {
        private final MapPlus<K, V> source;
        private final Ordering ordering;

        LiveAdapter(MapPlus<K, V> source, Ordering ordering) {
            this.source = source;
            this.ordering = ordering;
        }

        @Override
        public Set<Entry<K, V>> entrySet() {
            List<Entry<K, V>> entries = new ArrayList<>(source.entrySet());
            if (ordering == Ordering.ASCENDING || ordering == Ordering.DESCENDING) {
                entries.sort((a, b) -> {
                    @SuppressWarnings("unchecked")
                    Comparable<Object> ka = (Comparable<Object>) a.getKey();
                    int cmp = ka.compareTo(b.getKey());
                    return ordering == Ordering.DESCENDING ? -cmp : cmp;
                });
            }
            // INSERTION: natural order of the source (no sort needed)
            LinkedHashSet<Entry<K, V>> result = new LinkedHashSet<>(entries);
            return Collections.unmodifiableSet(result);
        }

        @Override
        public V get(Object key) {
            return source.get(key);
        }

        @Override
        public boolean containsKey(Object key) {
            return source.containsKey(key);
        }

        @Override
        public int size() {
            return source.size();
        }
    }
}
