package io.accord.collections.map;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MapPlusContractTest {

    static Stream<MapPlus<String, Integer>> impls() {
        return Stream.of(
                new StandardMapPlus<>(),
                new SortedMapPlus<>(Ordering.ASCENDING)
        );
    }

    @ParameterizedTest
    @MethodSource("impls")
    void putAndGet(MapPlus<String, Integer> map) {
        map.put("a", 1);
        assertEquals(1, map.get("a"));
    }

    @ParameterizedTest
    @MethodSource("impls")
    void nullKeyRejected(MapPlus<String, Integer> map) {
        NullPointerException ex = assertThrows(NullPointerException.class, () -> map.put(null, 1));
        assertNotNull(ex.getMessage());
    }

    @ParameterizedTest
    @MethodSource("impls")
    void nullValueRejected(MapPlus<String, Integer> map) {
        NullPointerException ex = assertThrows(NullPointerException.class, () -> map.put("k", null));
        assertNotNull(ex.getMessage());
    }

    @ParameterizedTest
    @MethodSource("impls")
    void getNullReturnsNull(MapPlus<String, Integer> map) {
        map.put("a", 1);
        assertNull(map.get(null));
    }

    @ParameterizedTest
    @MethodSource("impls")
    void containsKeyNullReturnsFalse(MapPlus<String, Integer> map) {
        map.put("a", 1);
        assertFalse(map.containsKey(null));
    }

    @ParameterizedTest
    @MethodSource("impls")
    void size(MapPlus<String, Integer> map) {
        assertEquals(0, map.size());
        map.put("a", 1);
        map.put("b", 2);
        assertEquals(2, map.size());
    }

    @ParameterizedTest
    @MethodSource("impls")
    void remove(MapPlus<String, Integer> map) {
        map.put("a", 1);
        map.remove("a");
        assertFalse(map.containsKey("a"));
        assertEquals(0, map.size());
    }

    @ParameterizedTest
    @MethodSource("impls")
    void equalsAndHashCode(MapPlus<String, Integer> map) {
        map.put("a", 1);
        MapPlus<String, Integer> other = new StandardMapPlus<>();
        other.put("a", 1);
        assertEquals(map, other);
        assertEquals(map.hashCode(), other.hashCode());
    }

    @ParameterizedTest
    @MethodSource("impls")
    void isEmpty(MapPlus<String, Integer> map) {
        assertTrue(map.isEmpty());
        map.put("x", 99);
        assertFalse(map.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("impls")
    void clear(MapPlus<String, Integer> map) {
        map.put("a", 1);
        map.clear();
        assertTrue(map.isEmpty());
    }
}
