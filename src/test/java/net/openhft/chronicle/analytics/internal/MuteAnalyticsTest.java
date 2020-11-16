package net.openhft.chronicle.analytics.internal;

import net.openhft.chronicle.analytics.Analytics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class MuteAnalyticsTest {

    @Test
    void sendEvent() {
        final Analytics analytics = new MuteAnalytics();
        assertDoesNotThrow(() -> analytics.sendEvent("a"));
    }
}