package io.accord.collections.map.view;

import io.accord.collections.map.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProjectedViewTest {

    @Test
    void projectValues() {
        StandardMapPlus<String, Integer> map = new StandardMapPlus<>();
        map.put("a", 1); map.put("b", 2);
        MapPlus<String, String> view = map.project(i -> "v" + i);
        assertEquals("v1", view.get("a"));
        assertEquals("v2", view.get("b"));
    }

    @Test
    void liveReflectsChange() {
        StandardMapPlus<String, Integer> map = new StandardMapPlus<>();
        map.put("a", 1);
        MapPlus<String, String> view = map.project(i -> "v" + i);
        map.put("a", 42);
        assertEquals("v42", view.get("a"));
    }

    @Test
    void missingKeyReturnsNull() {
        StandardMapPlus<String, Integer> map = new StandardMapPlus<>();
        MapPlus<String, String> view = map.project(Object::toString);
        assertNull(view.get("missing"));
    }

    @Test
    void readOnly() {
        StandardMapPlus<String, Integer> map = new StandardMapPlus<>();
        MapPlus<String, String> view = map.project(Object::toString);
        assertThrows(UnsupportedOperationException.class, () -> view.put("k", "v"));
        assertThrows(UnsupportedOperationException.class, view::clear);
    }

    @Test
    void sizeMatchesSource() {
        StandardMapPlus<String, Integer> map = new StandardMapPlus<>();
        map.put("a", 1); map.put("b", 2);
        MapPlus<String, String> view = map.project(Object::toString);
        assertEquals(2, view.size());
    }

    @Test
    void composedFilterThenProject() {
        StandardMapPlus<String, Integer> map = new StandardMapPlus<>();
        map.put("a", 1); map.put("b", 2); map.put("c", 3);
        MapPlus<String, String> view = map.filter(v -> v > 1).project(i -> "v" + i);
        assertNull(view.get("a"));
        assertEquals("v2", view.get("b"));
        assertEquals("v3", view.get("c"));
    }
}
