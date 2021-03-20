package net.openhft.chronicle.analytics.internal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class JUnitUtilTest {

    @Test
    void isJUnitAvailable() {
        assertTrue(JUnitUtil.isJUnitAvailable());
    }

    @Test
    void isClassAvailableString() {
            assertTrue(JUnitUtil.isClassAvailable(String.class.getName()));
    }

    @Test
    void isClassAvailableInventedName() {
        assertFalse(JUnitUtil.isClassAvailable("VeryUnliKELyNameToBeAnExisTINgClazz"));
    }
}