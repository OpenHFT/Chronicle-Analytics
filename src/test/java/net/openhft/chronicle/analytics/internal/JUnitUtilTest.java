/*
 * Copyright 2013-2025 chronicle.software; SPDX-License-Identifier: Apache-2.0
 */
package net.openhft.chronicle.analytics.internal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class JUnitUtilTest {

    @Test
    void isJUnitAvailable() {
        assertTrue(JUnitUtil.isJUnitAvailable(), "JUnit is available on the classpath");
    }

    @Test
    void isClassAvailableString() {
        assertTrue(JUnitUtil.isClassAvailable(String.class.getName()), "String is available");
    }

    @Test
    void isClassAvailableInventedName() {
        assertFalse(JUnitUtil.isClassAvailable("VeryUnliKELyNameToBeAnExisTINgClazz"), "invented class name not found");
    }
}
