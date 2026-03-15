package io.accord.collections.map.view;

import io.accord.collections.map.AbstractMapPlus;
import io.accord.collections.map.MapPlus;

import java.util.*;
import java.util.function.Function;

/**
 * Live value-projected view. Values are transformed on every access.
 */
public class ProjectedView<K, V, W> extends AbstractMapPlus<K, W> {

    private final MapPlus<K, V> source;
    private final Function<? super V, ? extends W> projection;

    public ProjectedView(MapPlus<K, V> source, Function<? super V, ? extends W> projection) {
        super(new LiveAdapter<>(source, projection));
        this.source = source;
        this.projection = projection;
    }

    @Override
    public W get(Object key) {
        if (key == null) return null;
        V v = source.get(key);
        return v == null ? null : projection.apply(v);
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
        private final MapPlus<K, V> source;
        private final Function<? super V, ? extends W> projection;

        LiveAdapter(MapPlus<K, V> source, Function<? super V, ? extends W> projection) {
            this.source = source;
            this.projection = projection;
        }

        @Override
        public Set<Entry<K, W>> entrySet() {
            return new AbstractSet<>() {
                @Override
                public Iterator<Entry<K, W>> iterator() {
                    Iterator<Entry<K, V>> it = source.entrySet().iterator();
                    return new Iterator<>() {
                        @Override public boolean hasNext() { return it.hasNext(); }
                        @Override public Entry<K, W> next() {
                            Entry<K, V> e = it.next();
                            W projected = e.getValue() == null ? null : projection.apply(e.getValue());
                            return Map.entry(e.getKey(), projected);
                        }
                    };
                }

                @Override
                public int size() { return source.size(); }
            };
        }

        @Override
        public W get(Object key) {
            if (key == null) return null;
            V v = source.get(key);
            return v == null ? null : projection.apply(v);
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
