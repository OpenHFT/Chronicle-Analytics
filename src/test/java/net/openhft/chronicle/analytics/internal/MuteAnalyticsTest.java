package net.openhft.chronicle.analytics.internal;

import net.openhft.chronicle.analytics.Analytics;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

final class MuteAnalyticsTest {

    @Test
    void sendEvent() {
        final Analytics analytics = MuteAnalytics.INSTANCE;
        int events = MuteAnalytics.INSTANCE.mutedEvents;
        assertDoesNotThrow(() -> analytics.sendEvent("a"));
        analytics.sendEvent("a"); // check it doesn't throw.
        analytics.sendEvent("a", Collections.emptyMap()); // check it doesn't throw.
        // keep Sonar happy
        assertEquals(2, MuteAnalytics.INSTANCE.mutedEvents - events);
    }
}