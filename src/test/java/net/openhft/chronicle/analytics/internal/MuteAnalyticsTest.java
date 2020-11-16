package net.openhft.chronicle.analytics.internal;

import net.openhft.chronicle.analytics.Analytics;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

final class MuteAnalyticsTest {

    @Test
    void sendEvent() {
        final Analytics analytics = MuteAnalytics.INSTANCE;
        assertDoesNotThrow(() -> analytics.sendEvent("a"));
        analytics.sendEvent("a"); // check it doesn't throw.
    }
}