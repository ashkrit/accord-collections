package io.accord.collections.map;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StandardMapPlusTest {

    @Test
    void preservesInsertionOrder() {
        StandardMapPlus<String, Integer> map = new StandardMapPlus<>();
        map.put("c", 3);
        map.put("a", 1);
        map.put("b", 2);
        assertEquals(List.of("c", "a", "b"), List.copyOf(map.keySet()));
    }

    @Test
    void overwriteKeepsOrder() {
        StandardMapPlus<String, Integer> map = new StandardMapPlus<>();
        map.put("a", 1);
        map.put("b", 2);
        map.put("a", 99); // overwrite, position stays
        assertEquals(List.of("a", "b"), List.copyOf(map.keySet()));
        assertEquals(99, map.get("a"));
    }

    @Test
    void emptyFactory() {
        MapPlus<String, Integer> map = MapPlus.empty();
        assertTrue(map.isEmpty());
    }

    @Test
    void ofFactory() {
        MapPlus<String, Integer> map = MapPlus.of(
                java.util.Map.entry("x", 10),
                java.util.Map.entry("y", 20)
        );
        assertEquals(10, map.get("x"));
        assertEquals(20, map.get("y"));
    }
}
