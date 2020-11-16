package net.openhft.chronicle.analytics.internal;

import net.openhft.chronicle.analytics.Analytics;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class VanillaAnalyticsBuilderTest {

    private static final String MEASUREMENT_ID = "M";
    private static final String API_SECRET = "S";
    private static final String TEST_STRING0 = "kisdf13273g111sJHJH";
    private static final String TEST_STRING1 = "23084kjgheai7tahk22";
    private static final Consumer<String> TEST_LOGGER = System.out::println;

    @Test
    void putUserProperty() {
        final VanillaAnalyticsBuilder builder = newInstance();
        builder.putUserProperty(TEST_STRING0, TEST_STRING1);
        assertEquals(Collections.singletonMap(TEST_STRING0, TEST_STRING1), builder.userProperties());
    }

    @Test
    void putEventParameter() {
        final VanillaAnalyticsBuilder builder = newInstance();
        builder.putEventParameter(TEST_STRING0, TEST_STRING1);
        assertEquals(Collections.singletonMap(TEST_STRING0, TEST_STRING1), builder.eventParameters());
    }

    @Test
    void withFrequencyLimit() {
        final Analytics.Builder builder = newInstance();
        assertThrows(IllegalArgumentException.class, () ->
                builder.withFrequencyLimit(-1, TimeUnit.SECONDS)
        );
    }

    @Test
    void withErrorLogger() {
        assertSame(TEST_LOGGER, ((VanillaAnalyticsBuilder) newInstance().withErrorLogger(TEST_LOGGER)).errorLogger());
    }

    @Test
    void withDebugLogger() {
        assertSame(TEST_LOGGER, ((VanillaAnalyticsBuilder) newInstance().withDebugLogger(TEST_LOGGER)).debugLogger());
    }

    @Test
    void withClientIdFileName() {
        assertEquals(TEST_STRING0, ((VanillaAnalyticsBuilder) newInstance().withClientIdFileName(TEST_STRING0)).clientIdFileName());
    }

    @Test
    void build() {
        assertNotNull(newInstance().build());

        final Analytics.Builder builder = newInstance();
        builder.build();
        // should fail the second time
        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    void measurementId() {
        assertNotNull(newInstance().measurementId());
        assertEquals(MEASUREMENT_ID, newInstance().measurementId());
    }

    @Test
    void apiSecret() {
        assertNotNull(newInstance().apiSecret());
        assertEquals(API_SECRET, newInstance().apiSecret());
    }

    @Test
    void userProperties() {
        assertNotNull(newInstance().userProperties());
        assertTrue(newInstance().userProperties().isEmpty());
    }

    @Test
    void eventParameters() {
        assertNotNull(newInstance().eventParameters());
        assertTrue(newInstance().eventParameters().isEmpty());
    }

    @Test
    void errorLogger() {
        assertNotNull(newInstance().errorLogger());
    }

    @Test
    void debugLogger() {
        assertNotNull(newInstance().debugLogger());
    }

    @Test
    void duration() {
        assertEquals(0L, newInstance().duration());
    }

    @Test
    void timeUnit() {
        assertNotNull(newInstance().timeUnit());
    }

    @Test
    void fileName() {
        final String defaultFileName = System.getProperty("user.home") + "/chronicle.analytics.client.id";
        assertEquals(defaultFileName, newInstance().clientIdFileName());
    }

    private VanillaAnalyticsBuilder newInstance() {
        return new VanillaAnalyticsBuilder(MEASUREMENT_ID, API_SECRET);
    }

    private void assertDefaultNotNull(@NotNull final Function<? super AnalyticsConfiguration, Object> getter) {
        assertNotNull(getter.apply(newInstance()));
    }

}