package io.accord.collections.map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NullSafetyTest {

    private final MapPlus<String, Integer> map = new StandardMapPlus<>();

    @Test void putNullKey()   { var e = assertThrows(NullPointerException.class, () -> map.put(null, 1));   assertNotNull(e.getMessage()); }
    @Test void putNullValue() { var e = assertThrows(NullPointerException.class, () -> map.put("k", null)); assertNotNull(e.getMessage()); }
    @Test void getNullKey()   { assertNull(map.get(null)); }
    @Test void containsNullKey() { assertFalse(map.containsKey(null)); }

    @Test void viewNullOrdering()     { var e = assertThrows(NullPointerException.class, () -> map.view(null));           assertNotNull(e.getMessage()); }
    @Test void projectNullFunction()  { var e = assertThrows(NullPointerException.class, () -> map.project(null));        assertNotNull(e.getMessage()); }
    @Test void filterNullPredicate()  { var e = assertThrows(NullPointerException.class, () -> map.filter(null));         assertNotNull(e.getMessage()); }
    @Test void thenNullNext()         { var e = assertThrows(NullPointerException.class, () -> map.then(null));           assertNotNull(e.getMessage()); }
    @Test void mergeNullOther()       { var e = assertThrows(NullPointerException.class, () -> map.merge(null, MergeStrategy.PREFER_LEFT)); assertNotNull(e.getMessage()); }
    @Test void mergeNullStrategy()    { var e = assertThrows(NullPointerException.class, () -> map.merge(new StandardMapPlus<>(), null));   assertNotNull(e.getMessage()); }
}
