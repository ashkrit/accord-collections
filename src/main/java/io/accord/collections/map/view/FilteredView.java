package io.accord.collections.map.view;

import io.accord.collections.map.AbstractMapPlus;
import io.accord.collections.map.MapPlus;

import java.util.*;
import java.util.function.Predicate;

/**
 * Live filtered view. Only entries whose value matches the predicate are visible.
 */
public class FilteredView<K, V> extends AbstractMapPlus<K, V> {

    private final MapPlus<K, V> source;
    private final Predicate<? super V> predicate;

    public FilteredView(MapPlus<K, V> source, Predicate<? super V> predicate) {
        super(new LiveAdapter<>(source, predicate));
        this.source = source;
        this.predicate = predicate;
    }

    @Override
    public V get(Object key) {
        if (key == null) return null;
        V v = source.get(key);
        return (v != null && predicate.test(v)) ? v : null;
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) return false;
        V v = source.get(key);
        return v != null && predicate.test(v);
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
        private final Predicate<? super V> predicate;

        LiveAdapter(MapPlus<K, V> source, Predicate<? super V> predicate) {
            this.source = source;
            this.predicate = predicate;
        }

        @Override
        public Set<Entry<K, V>> entrySet() {
            return new AbstractSet<>() {
                @Override
                public Iterator<Entry<K, V>> iterator() {
                    Iterator<Entry<K, V>> it = source.entrySet().iterator();
                    return new Iterator<>() {
                        private Entry<K, V> next = null;

                        private void advance() {
                            while (next == null && it.hasNext()) {
                                Entry<K, V> e = it.next();
                                if (e.getValue() != null && predicate.test(e.getValue())) {
                                    next = e;
                                }
                            }
                        }

                        @Override
                        public boolean hasNext() {
                            advance();
                            return next != null;
                        }

                        @Override
                        public Entry<K, V> next() {
                            advance();
                            if (next == null) throw new NoSuchElementException();
                            Entry<K, V> result = next;
                            next = null;
                            return result;
                        }
                    };
                }

                @Override
                public int size() {
                    int count = 0;
                    for (Entry<K, V> e : source.entrySet()) {
                        if (e.getValue() != null && predicate.test(e.getValue())) count++;
                    }
                    return count;
                }
            };
        }

        @Override
        public V get(Object key) {
            if (key == null) return null;
            V v = source.get(key);
            return (v != null && predicate.test(v)) ? v : null;
        }

        @Override
        public boolean containsKey(Object key) {
            if (key == null) return false;
            V v = source.get(key);
            return v != null && predicate.test(v);
        }
    }
}
