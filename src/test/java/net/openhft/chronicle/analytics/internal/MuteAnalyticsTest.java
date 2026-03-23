/*
 * Copyright 2013-2025 chronicle.software; SPDX-License-Identifier: Apache-2.0
 */
package net.openhft.chronicle.analytics.internal;

import net.openhft.chronicle.analytics.Analytics;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

final class MuteAnalyticsTest {

    @Test
    void sendEvent() {
        final Analytics analytics = MuteAnalytics.INSTANCE;
        int events = MuteAnalytics.INSTANCE.mutedEvents;
        analytics.sendEvent("a");
        analytics.sendEvent("a", Collections.emptyMap());
        // keep Sonar happy
        assertEquals(2, MuteAnalytics.INSTANCE.mutedEvents - events);
    }
}
