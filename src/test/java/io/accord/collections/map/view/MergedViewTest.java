package io.accord.collections.map.view;

import io.accord.collections.map.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MergedViewTest {

    private MapPlus<String, Integer> left() {
        StandardMapPlus<String, Integer> m = new StandardMapPlus<>();
        m.put("a", 1); m.put("shared", 10);
        return m;
    }

    private MapPlus<String, Integer> right() {
        StandardMapPlus<String, Integer> m = new StandardMapPlus<>();
        m.put("b", 2); m.put("shared", 20);
        return m;
    }

    @Test
    void preferLeft() {
        MapPlus<String, Integer> view = left().merge(right(), MergeStrategy.PREFER_LEFT);
        assertEquals(1, view.get("a"));
        assertEquals(2, view.get("b"));
        assertEquals(10, view.get("shared"));
    }

    @Test
    void preferRight() {
        MapPlus<String, Integer> view = left().merge(right(), MergeStrategy.PREFER_RIGHT);
        assertEquals(1, view.get("a"));
        assertEquals(2, view.get("b"));
        assertEquals(20, view.get("shared"));
    }

    @Test
    void throwOnConflict() {
        MapPlus<String, Integer> view = left().merge(right(), MergeStrategy.THROW);
        assertEquals(1, view.get("a"));
        assertEquals(2, view.get("b"));
        assertThrows(IllegalStateException.class, () -> view.get("shared"));
    }

    @Test
    void liveReflectsChange() {
        StandardMapPlus<String, Integer> l = new StandardMapPlus<>();
        l.put("a", 1);
        StandardMapPlus<String, Integer> r = new StandardMapPlus<>();

        MapPlus<String, Integer> view = l.merge(r, MergeStrategy.PREFER_LEFT);
        assertNull(view.get("b"));
        r.put("b", 99);
        assertEquals(99, view.get("b"));
    }

    @Test
    void sizeIsUnion() {
        MapPlus<String, Integer> view = left().merge(right(), MergeStrategy.PREFER_LEFT);
        // a, b, shared = 3 unique keys
        assertEquals(3, view.size());
    }

    @Test
    void readOnly() {
        MapPlus<String, Integer> view = left().merge(right(), MergeStrategy.PREFER_LEFT);
        assertThrows(UnsupportedOperationException.class, () -> view.put("x", 1));
        assertThrows(UnsupportedOperationException.class, view::clear);
        assertThrows(UnsupportedOperationException.class, () -> view.remove("a"));
    }

    @Test
    void containsKeyChecksUnion() {
        MapPlus<String, Integer> view = left().merge(right(), MergeStrategy.PREFER_LEFT);
        assertTrue(view.containsKey("a"));
        assertTrue(view.containsKey("b"));
        assertTrue(view.containsKey("shared"));
        assertFalse(view.containsKey("missing"));
    }
}
