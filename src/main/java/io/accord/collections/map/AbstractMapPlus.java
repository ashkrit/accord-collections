package io.accord.collections.map;

import io.accord.collections.map.view.*;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Abstract base that delegates all {@link Map} operations to a backing map,
 * enforces the null contract, and implements the five {@link MapPlus} operations.
 */
public abstract class AbstractMapPlus<K, V> implements MapPlus<K, V> {

    protected final Map<K, V> delegate;

    protected AbstractMapPlus(Map<K, V> delegate) {
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null");
    }

    // --- Null-enforcing mutations ---

    @Override
    public V put(K key, V value) {
        Objects.requireNonNull(key, "key must not be null");
        Objects.requireNonNull(value, "value must not be null");
        return delegate.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        Objects.requireNonNull(m, "map must not be null");
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    // --- Safe reads (null key → absent) ---

    @Override
    public V get(Object key) {
        if (key == null) return null;
        return delegate.get(key);
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) return false;
        return delegate.containsKey(key);
    }

    // --- Plain delegation ---

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public V remove(Object key) {
        return delegate.remove(key);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public Set<K> keySet() {
        return delegate.keySet();
    }

    @Override
    public Collection<V> values() {
        return delegate.values();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return delegate.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    // --- MapPlus operations ---

    @Override
    public MapPlus<K, V> view(Ordering ordering) {
        Objects.requireNonNull(ordering, "ordering must not be null");
        return new OrderedView<>(this, ordering);
    }

    @Override
    public <W> MapPlus<K, W> project(Function<? super V, ? extends W> projection) {
        Objects.requireNonNull(projection, "projection must not be null");
        return new ProjectedView<>(this, projection);
    }

    @Override
    public MapPlus<K, V> filter(Predicate<? super V> predicate) {
        Objects.requireNonNull(predicate, "predicate must not be null");
        return new FilteredView<>(this, predicate);
    }

    @Override
    public <W> MapPlus<K, W> then(MapPlus<V, W> next) {
        Objects.requireNonNull(next, "next must not be null");
        return new ChainedView<>(this, next);
    }

    @Override
    public MapPlus<K, V> merge(MapPlus<K, V> other, MergeStrategy strategy) {
        Objects.requireNonNull(other, "other must not be null");
        Objects.requireNonNull(strategy, "strategy must not be null");
        return new MergedView<>(this, other, strategy);
    }
}
