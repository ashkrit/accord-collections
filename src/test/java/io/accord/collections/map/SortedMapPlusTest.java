package io.accord.collections.map;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SortedMapPlusTest {

    @Test
    void ascendingOrder() {
        SortedMapPlus<String, Integer> map = new SortedMapPlus<>(Ordering.ASCENDING);
        map.put("c", 3);
        map.put("a", 1);
        map.put("b", 2);
        assertEquals(List.of("a", "b", "c"), List.copyOf(map.keySet()));
    }

    @Test
    void descendingOrder() {
        SortedMapPlus<String, Integer> map = new SortedMapPlus<>(Ordering.DESCENDING);
        map.put("c", 3);
        map.put("a", 1);
        map.put("b", 2);
        assertEquals(List.of("c", "b", "a"), List.copyOf(map.keySet()));
    }

    @Test
    void insertionOrderRejected() {
        assertThrows(IllegalArgumentException.class, () -> new SortedMapPlus<>(Ordering.INSERTION));
    }

    @Test
    void nullKeyRejected() {
        SortedMapPlus<String, Integer> map = new SortedMapPlus<>(Ordering.ASCENDING);
        assertThrows(NullPointerException.class, () -> map.put(null, 1));
    }
}
