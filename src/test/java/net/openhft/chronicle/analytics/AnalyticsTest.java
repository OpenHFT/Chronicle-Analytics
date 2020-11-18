package net.openhft.chronicle.analytics;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AnalyticsTest {

    private static final String TEST_STRING = "Harry";

    @Test
    void sendEvent() {
        final AtomicReference<String> sendName = new AtomicReference<>();
        final Analytics analytics = (name, additionalEventParameters) -> sendName.set(name);

        analytics.sendEvent(TEST_STRING);
        assertEquals(TEST_STRING, sendName.get());
    }

    @Test
    void builder() {
        Analytics.Builder builder= Analytics.builder(TEST_STRING, TEST_STRING);
        assertNotNull(builder);
    }
}