package io.accord.collections.map.view;

import io.accord.collections.map.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChainedViewTest {

    @Test
    void chainsLookup() {
        StandardMapPlus<String, String> left = new StandardMapPlus<>();
        left.put("user1", "role_admin");
        StandardMapPlus<String, String> right = new StandardMapPlus<>();
        right.put("role_admin", "Administrator");

        MapPlus<String, String> view = left.then(right);
        assertEquals("Administrator", view.get("user1"));
    }

    @Test
    void missingIntermediateReturnsNull() {
        StandardMapPlus<String, String> left = new StandardMapPlus<>();
        left.put("user1", "role_unknown");
        StandardMapPlus<String, String> right = new StandardMapPlus<>();
        right.put("role_admin", "Administrator");

        MapPlus<String, String> view = left.then(right);
        assertNull(view.get("user1"));
    }

    @Test
    void missingLeftKeyReturnsNull() {
        StandardMapPlus<String, String> left = new StandardMapPlus<>();
        StandardMapPlus<String, String> right = new StandardMapPlus<>();
        right.put("role_admin", "Administrator");

        MapPlus<String, String> view = left.then(right);
        assertNull(view.get("user1"));
    }

    @Test
    void liveReflectsChange() {
        StandardMapPlus<String, String> left = new StandardMapPlus<>();
        left.put("k", "intermediate");
        StandardMapPlus<String, String> right = new StandardMapPlus<>();

        MapPlus<String, String> view = left.then(right);
        assertNull(view.get("k"));

        right.put("intermediate", "final");
        assertEquals("final", view.get("k"));
    }

    @Test
    void sizeCountsOnlyChainable() {
        StandardMapPlus<String, String> left = new StandardMapPlus<>();
        left.put("k1", "v1"); left.put("k2", "v2");
        StandardMapPlus<String, String> right = new StandardMapPlus<>();
        right.put("v1", "result");

        MapPlus<String, String> view = left.then(right);
        assertEquals(1, view.size());
    }

    @Test
    void readOnly() {
        StandardMapPlus<String, String> left = new StandardMapPlus<>();
        StandardMapPlus<String, String> right = new StandardMapPlus<>();
        MapPlus<String, String> view = left.then(right);
        assertThrows(UnsupportedOperationException.class, () -> view.put("k", "v"));
        assertThrows(UnsupportedOperationException.class, view::clear);
    }
}
