package io.accord.collections.map;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    void fromMapContainsAllEntries() {
        Map<String, Integer> source = new HashMap<>();
        source.put("x", 10);
        source.put("y", 20);
        StandardMapPlus<String, Integer> map = new StandardMapPlus<>(source);
        assertEquals(10, map.get("x"));
        assertEquals(20, map.get("y"));
        assertEquals(2, map.size());
    }

    @Test
    void fromMapWrapsSourceLive() {
        // source is used as the delegate directly — mutations to source are visible
        Map<String, Integer> source = new HashMap<>();
        source.put("a", 1);
        StandardMapPlus<String, Integer> map = new StandardMapPlus<>(source);
        source.put("b", 2);
        assertEquals(2, map.get("b"));
        assertEquals(2, map.size());
    }

    @Test
    void fromMapNullKeyReturnsNull() {
        Map<String, Integer> source = new HashMap<>();
        source.put("a", 1);
        StandardMapPlus<String, Integer> map = new StandardMapPlus<>(source);
        assertNull(map.get(null));
        assertFalse(map.containsKey(null));
    }

    @Test
    void fromMapPutStillEnforcesNullContract() {
        Map<String, Integer> source = new HashMap<>();
        source.put("a", 1);
        StandardMapPlus<String, Integer> map = new StandardMapPlus<>(source);
        assertThrows(NullPointerException.class, () -> map.put(null, 99));
        assertThrows(NullPointerException.class, () -> map.put("b", null));
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
