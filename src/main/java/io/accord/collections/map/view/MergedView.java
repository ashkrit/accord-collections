package io.accord.collections.map.view;

import io.accord.collections.map.AbstractMapPlus;
import io.accord.collections.map.MapPlus;
import io.accord.collections.map.MergeStrategy;

import java.util.*;

/**
 * Live merged view of two maps. Key conflicts resolved per {@link MergeStrategy}.
 */
public class MergedView<K, V> extends AbstractMapPlus<K, V> {

    private final MapPlus<K, V> left;
    private final MapPlus<K, V> right;
    private final MergeStrategy strategy;

    public MergedView(MapPlus<K, V> left, MapPlus<K, V> right, MergeStrategy strategy) {
        super(new LiveAdapter<>(left, right, strategy));
        this.left = left;
        this.right = right;
        this.strategy = strategy;
    }

    @Override
    public V get(Object key) {
        if (key == null) return null;
        boolean inLeft = left.containsKey(key);
        boolean inRight = right.containsKey(key);
        return switch (strategy) {
            case PREFER_LEFT -> inLeft ? left.get(key) : right.get(key);
            case PREFER_RIGHT -> inRight ? right.get(key) : left.get(key);
            case THROW -> {
                if (inLeft && inRight)
                    throw new IllegalStateException("Key conflict: " + key);
                yield inLeft ? left.get(key) : right.get(key);
            }
        };
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) return false;
        return left.containsKey(key) || right.containsKey(key);
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
        private final MapPlus<K, V> left;
        private final MapPlus<K, V> right;
        private final MergeStrategy strategy;

        LiveAdapter(MapPlus<K, V> left, MapPlus<K, V> right, MergeStrategy strategy) {
            this.left = left;
            this.right = right;
            this.strategy = strategy;
        }

        @Override
        public Set<Entry<K, V>> entrySet() {
            return new AbstractSet<>() {
                @Override
                public Iterator<Entry<K, V>> iterator() {
                    // left keys first, then right-only keys
                    Set<K> seen = new LinkedHashSet<>(left.keySet());
                    List<Entry<K, V>> entries = new ArrayList<>();
                    for (K k : left.keySet()) {
                        entries.add(Map.entry(k, resolve(k)));
                    }
                    for (Map.Entry<K, V> e : right.entrySet()) {
                        if (!seen.contains(e.getKey())) {
                            entries.add(Map.entry(e.getKey(), resolve(e.getKey())));
                        }
                    }
                    return entries.iterator();
                }

                private V resolve(K key) {
                    boolean inLeft = left.containsKey(key);
                    boolean inRight = right.containsKey(key);
                    return switch (strategy) {
                        case PREFER_LEFT -> inLeft ? left.get(key) : right.get(key);
                        case PREFER_RIGHT -> inRight ? right.get(key) : left.get(key);
                        case THROW -> {
                            if (inLeft && inRight)
                                throw new IllegalStateException("Key conflict: " + key);
                            yield inLeft ? left.get(key) : right.get(key);
                        }
                    };
                }

                @Override
                public int size() {
                    Set<K> union = new HashSet<>(left.keySet());
                    union.addAll(right.keySet());
                    return union.size();
                }
            };
        }

        @Override
        public V get(Object key) {
            if (key == null) return null;
            boolean inLeft = left.containsKey(key);
            boolean inRight = right.containsKey(key);
            return switch (strategy) {
                case PREFER_LEFT -> inLeft ? left.get(key) : right.get(key);
                case PREFER_RIGHT -> inRight ? right.get(key) : left.get(key);
                case THROW -> {
                    if (inLeft && inRight)
                        throw new IllegalStateException("Key conflict: " + key);
                    yield inLeft ? left.get(key) : right.get(key);
                }
            };
        }

        @Override
        public boolean containsKey(Object key) {
            return left.containsKey(key) || right.containsKey(key);
        }

        @Override
        public int size() {
            Set<K> union = new HashSet<>(left.keySet());
            union.addAll(right.keySet());
            return union.size();
        }
    }
}
