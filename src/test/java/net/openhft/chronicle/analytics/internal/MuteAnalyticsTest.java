package net.openhft.chronicle.analytics.internal;

import net.openhft.chronicle.analytics.Analytics;
import org.junit.jupiter.api.Test;

final class MuteAnalyticsTest {

    @Test
    void sendEvent() {
        final Analytics analytics = MuteAnalytics.INSTANCE;
        analytics.sendEvent("a"); // check it doesn't throw.
    }
}