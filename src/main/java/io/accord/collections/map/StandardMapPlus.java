package io.accord.collections.map;

import java.util.LinkedHashMap;

/**
 * Concrete {@link MapPlus} backed by a {@link LinkedHashMap} (insertion order).
 */
public class StandardMapPlus<K, V> extends AbstractMapPlus<K, V> {

    public StandardMapPlus() {
        super(new LinkedHashMap<>());
    }
}
