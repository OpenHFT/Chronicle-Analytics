package net.openhft.chronicle.analytics.internal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class JUnitUtilTest {

    @Test
    void isJUnitAvailable() {
        assertTrue(JUnitUtil.isJUnitAvailable());
    }
}