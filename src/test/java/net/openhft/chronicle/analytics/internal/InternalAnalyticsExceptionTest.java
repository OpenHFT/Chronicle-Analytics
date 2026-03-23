/*
 * Copyright 2013-2025 chronicle.software; SPDX-License-Identifier: Apache-2.0
 */
package net.openhft.chronicle.analytics.internal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class InternalAnalyticsExceptionTest {

    private static final String MSG = "abc123";

    @Test
    void create() {
        final InternalAnalyticsException e = new InternalAnalyticsException(MSG);
        final String actual = e.getMessage();
        assertEquals(MSG, actual);
    }
}
