package io.accord.collections.map;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Concrete {@link MapPlus} backed by a {@link LinkedHashMap} (insertion order).
 */
public class StandardMapPlus<K, V> extends AbstractMapPlus<K, V> {

    public StandardMapPlus() {
        super(new LinkedHashMap<>());
    }

    public StandardMapPlus(Map<? extends K, ? extends V> source) {
        super((Map<K, V>) source);
    }
}
