package io.accord.collections.map.view;

import io.accord.collections.map.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderedViewTest {

    @Test
    void ascendingOrder() {
        StandardMapPlus<String, Integer> map = new StandardMapPlus<>();
        map.put("c", 3); map.put("a", 1); map.put("b", 2);
        MapPlus<String, Integer> view = map.view(Ordering.ASCENDING);
        assertEquals(List.of("a", "b", "c"), List.copyOf(view.keySet()));
    }

    @Test
    void descendingOrder() {
        StandardMapPlus<String, Integer> map = new StandardMapPlus<>();
        map.put("c", 3); map.put("a", 1); map.put("b", 2);
        MapPlus<String, Integer> view = map.view(Ordering.DESCENDING);
        assertEquals(List.of("c", "b", "a"), List.copyOf(view.keySet()));
    }

    @Test
    void liveReflectsChange() {
        StandardMapPlus<String, Integer> map = new StandardMapPlus<>();
        map.put("b", 2);
        MapPlus<String, Integer> view = map.view(Ordering.ASCENDING);
        map.put("a", 1);
        assertEquals(List.of("a", "b"), List.copyOf(view.keySet()));
    }

    @Test
    void readOnly() {
        StandardMapPlus<String, Integer> map = new StandardMapPlus<>();
        map.put("a", 1);
        MapPlus<String, Integer> view = map.view(Ordering.ASCENDING);
        assertThrows(UnsupportedOperationException.class, () -> view.put("z", 99));
        assertThrows(UnsupportedOperationException.class, view::clear);
        assertThrows(UnsupportedOperationException.class, () -> view.remove("a"));
    }

    @Test
    void insertionOrderView() {
        StandardMapPlus<String, Integer> map = new StandardMapPlus<>();
        map.put("c", 3); map.put("a", 1); map.put("b", 2);
        MapPlus<String, Integer> view = map.view(Ordering.INSERTION);
        assertEquals(List.of("c", "a", "b"), List.copyOf(view.keySet()));
    }
}
