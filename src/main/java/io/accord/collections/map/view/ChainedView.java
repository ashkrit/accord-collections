package io.accord.collections.map.view;

import io.accord.collections.map.AbstractMapPlus;
import io.accord.collections.map.MapPlus;

import java.util.*;

/**
 * Live chained view: keys come from {@code left}, values come from {@code right}
 * using {@code left}'s values as intermediate keys.
 */
public class ChainedView<K, V, W> extends AbstractMapPlus<K, W> {

    private final MapPlus<K, V> left;
    private final MapPlus<V, W> right;

    public ChainedView(MapPlus<K, V> left, MapPlus<V, W> right) {
        super(new LiveAdapter<>(left, right));
        this.left = left;
        this.right = right;
    }

    @Override
    public W get(Object key) {
        if (key == null) return null;
        V intermediate = left.get(key);
        if (intermediate == null) return null;
        return right.get(intermediate);
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) return false;
        V intermediate = left.get(key);
        if (intermediate == null) return false;
        return right.containsKey(intermediate);
    }

    @Override
    public W put(K key, W value) {
        throw new UnsupportedOperationException("View is read-only");
    }

    @Override
    public void putAll(Map<? extends K, ? extends W> m) {
        throw new UnsupportedOperationException("View is read-only");
    }

    @Override
    public W remove(Object key) {
        throw new UnsupportedOperationException("View is read-only");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("View is read-only");
    }

    private static class LiveAdapter<K, V, W> extends AbstractMap<K, W> {
        private final MapPlus<K, V> left;
        private final MapPlus<V, W> right;

        LiveAdapter(MapPlus<K, V> left, MapPlus<V, W> right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public Set<Entry<K, W>> entrySet() {
            return new AbstractSet<>() {
                @Override
                public Iterator<Entry<K, W>> iterator() {
                    Iterator<Entry<K, V>> it = left.entrySet().iterator();
                    return new Iterator<>() {
                        private Entry<K, W> next = null;

                        private void advance() {
                            while (next == null && it.hasNext()) {
                                Entry<K, V> e = it.next();
                                W w = right.get(e.getValue());
                                if (w != null) {
                                    next = Map.entry(e.getKey(), w);
                                }
                            }
                        }

                        @Override
                        public boolean hasNext() {
                            advance();
                            return next != null;
                        }

                        @Override
                        public Entry<K, W> next() {
                            advance();
                            if (next == null) throw new NoSuchElementException();
                            Entry<K, W> result = next;
                            next = null;
                            return result;
                        }
                    };
                }

                @Override
                public int size() {
                    int count = 0;
                    for (Entry<K, V> e : left.entrySet()) {
                        if (right.containsKey(e.getValue())) count++;
                    }
                    return count;
                }
            };
        }

        @Override
        public W get(Object key) {
            if (key == null) return null;
            V intermediate = left.get(key);
            if (intermediate == null) return null;
            return right.get(intermediate);
        }

        @Override
        public boolean containsKey(Object key) {
            if (key == null) return false;
            V intermediate = left.get(key);
            if (intermediate == null) return false;
            return right.containsKey(intermediate);
        }
    }
}
