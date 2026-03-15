package io.accord.collections.map.view;

import io.accord.collections.map.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FilteredViewTest {

    @Test
    void filtersEntries() {
        StandardMapPlus<String, Integer> map = new StandardMapPlus<>();
        map.put("a", 1); map.put("b", 2); map.put("c", 3);
        MapPlus<String, Integer> view = map.filter(v -> v > 1);
        assertNull(view.get("a"));
        assertEquals(2, view.get("b"));
        assertEquals(3, view.get("c"));
    }

    @Test
    void containsKeyRespectsPredicate() {
        StandardMapPlus<String, Integer> map = new StandardMapPlus<>();
        map.put("a", 1); map.put("b", 2);
        MapPlus<String, Integer> view = map.filter(v -> v > 1);
        assertFalse(view.containsKey("a"));
        assertTrue(view.containsKey("b"));
    }

    @Test
    void liveReflectsChange() {
        StandardMapPlus<String, Integer> map = new StandardMapPlus<>();
        map.put("a", 1);
        MapPlus<String, Integer> view = map.filter(v -> v > 1);
        assertNull(view.get("a"));
        map.put("a", 5);
        assertEquals(5, view.get("a"));
    }

    @Test
    void sizeOnlyCountsMatching() {
        StandardMapPlus<String, Integer> map = new StandardMapPlus<>();
        map.put("a", 1); map.put("b", 2); map.put("c", 3);
        MapPlus<String, Integer> view = map.filter(v -> v > 1);
        assertEquals(2, view.size());
    }

    @Test
    void readOnly() {
        StandardMapPlus<String, Integer> map = new StandardMapPlus<>();
        MapPlus<String, Integer> view = map.filter(v -> v > 0);
        assertThrows(UnsupportedOperationException.class, () -> view.put("k", 1));
        assertThrows(UnsupportedOperationException.class, view::clear);
        assertThrows(UnsupportedOperationException.class, () -> view.remove("k"));
    }
}
