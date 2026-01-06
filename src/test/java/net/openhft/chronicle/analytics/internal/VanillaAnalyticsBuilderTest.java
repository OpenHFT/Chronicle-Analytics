/*
 * Copyright 2013-2025 chronicle.software; SPDX-License-Identifier: Apache-2.0
 */
package net.openhft.chronicle.analytics.internal;

import net.openhft.chronicle.analytics.Analytics;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

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
        assertEquals(Collections.singletonMap(TEST_STRING0, TEST_STRING1), builder.userProperties(), "user properties map should contain the single key-value pair added via putUserProperty");
    }

    @Test
    void putEventParameter() {
        final VanillaAnalyticsBuilder builder = newInstance();
        builder.putEventParameter(TEST_STRING0, TEST_STRING1);
        assertEquals(Collections.singletonMap(TEST_STRING0, TEST_STRING1), builder.eventParameters(), "event parameters map should contain the single key-value pair added via putEventParameter");
    }

    @Test
    void withFrequencyLimit() {
        final Analytics.Builder builder = newInstance();
        assertThrows(IllegalArgumentException.class, () ->
                        builder.withFrequencyLimit(1, -1, TimeUnit.SECONDS),
                "frequency limit configuration must reject negative duration values as they represent invalid time periods"
        );
    }

    @Test
    void withFrequencyLimit2() {
        assertNotNull(newInstance().withFrequencyLimit(1, 1, TimeUnit.SECONDS), "frequency limit configuration with valid parameters should return a non-null builder instance for method chaining");
    }

    @Test
    void withErrorLogger() {
        assertSame(TEST_LOGGER, ((VanillaAnalyticsBuilder) newInstance().withErrorLogger(TEST_LOGGER)).errorLogger(), "error logger configuration should preserve the exact consumer instance provided for debugging analytics failures");
    }

    @Test
    void withErrorLoggerSuper() {
        final AtomicReference<CharSequence> reference = new AtomicReference<>();
        final Consumer<? super String> consumer = (Consumer<CharSequence>) reference::set;

        final Consumer<String> returnedConsumer = ((VanillaAnalyticsBuilder) newInstance().withErrorLogger(consumer)).errorLogger();

        final String s = "Tryggve";

        returnedConsumer.accept(s);
        assertEquals(s, reference.get(), "error logger with super-type consumer should correctly forward string messages to the underlying consumer reference");
    }

    @Test
    void withDebugLogger() {
        assertSame(TEST_LOGGER, ((VanillaAnalyticsBuilder) newInstance().withDebugLogger(TEST_LOGGER)).debugLogger(), "debug logger configuration should preserve the exact consumer instance provided for troubleshooting analytics operations");
    }

    @Test
    void withClientIdFileName() {
        assertEquals(TEST_STRING0, ((VanillaAnalyticsBuilder) newInstance().withClientIdFileName(TEST_STRING0)).clientIdFileName(), "client ID file name configuration should store the custom file path for persisting stable user identifiers");
    }

    @Test
    void withReportDespiteJUnit() {
        assertInstanceOf(GoogleAnalytics4.class, newInstance().withReportDespiteJUnit().build(), "builder configured to report despite JUnit detection should create an active GoogleAnalytics4 instance instead of muted analytics");
    }

    @Test
    void withReportDespiteJUnit2() {
        final Analytics analytics = newInstance().build();
        assertInstanceOf(MuteAnalytics.class, analytics, "default builder without explicit override should automatically create MuteAnalytics when running under JUnit to prevent test pollution");
    }

    @Test
    void withUrl() {
        assertEquals(TEST_STRING0, ((VanillaAnalyticsBuilder) newInstance().withUrl(TEST_STRING0)).url(), "custom URL configuration should store the endpoint address for directing analytics HTTP requests to alternative servers");
    }

    @Test
    void build() {
        assertNotNull(newInstance().build(), "builder should produce a non-null Analytics instance configured according to the builder settings");

        final Analytics.Builder builder = newInstance();
        builder.build();
        // should fail the second time
        assertThrows(IllegalStateException.class, builder::build, "builder should enforce single-use pattern by throwing IllegalStateException on second build attempt to prevent configuration reuse bugs");
    }

    @Test
    void measurementId() {
        assertNotNull(newInstance().measurementId(), "measurement ID getter should return a non-null value as it is a required constructor parameter");
        assertEquals(MEASUREMENT_ID, newInstance().measurementId(), "measurement ID getter should return the exact value provided to the builder constructor");
    }

    @Test
    void apiSecret() {
        assertNotNull(newInstance().apiSecret(), "API secret getter should return a non-null value as it is a required constructor parameter for authenticating with Google Analytics");
        assertEquals(API_SECRET, newInstance().apiSecret(), "API secret getter should return the exact value provided to the builder constructor");
    }

    @Test
    void userProperties() {
        assertNotNull(newInstance().userProperties(), "user properties getter should return a non-null map instance to allow inspection of configured properties");
        assertTrue(newInstance().userProperties().isEmpty(), "newly constructed builder should have an empty user properties map before any properties are added");
    }

    @Test
    void eventParameters() {
        assertNotNull(newInstance().eventParameters(), "event parameters getter should return a non-null map instance to allow inspection of configured parameters");
        assertTrue(newInstance().eventParameters().isEmpty(), "newly constructed builder should have an empty event parameters map before any parameters are added");
    }

    @Test
    void errorLogger() {
        assertNotNull(newInstance().errorLogger(), "error logger getter should return a non-null default consumer to ensure analytics failures can always be logged");
    }

    @Test
    void debugLogger() {
        assertNotNull(newInstance().debugLogger(), "debug logger getter should return a non-null default consumer to ensure analytics operations can always be traced");
    }

    @Test
    void duration() {
        assertEquals(0L, newInstance().duration(), "newly constructed builder should have zero duration indicating no frequency limit is configured by default");
    }

    @Test
    void timeUnit() {
        assertNotNull(newInstance().timeUnit(), "time unit getter should return a non-null default value even when no frequency limit is configured");
    }

    @Test
    void fileName() {
        final String defaultFileName = System.getProperty("user.home") + "/.chronicle.analytics.client.id";
        assertEquals(defaultFileName, newInstance().clientIdFileName(), "default client ID file path should be constructed from user home directory with standard hidden file naming convention");
    }

    @Test
    void url() {
        final String defaultUrl = "https://www.google-analytics.com/mp/collect";
        assertEquals(defaultUrl, newInstance().url(), "default URL should point to the standard Google Analytics 4 measurement protocol collection endpoint");
    }

    private VanillaAnalyticsBuilder newInstance() {
        return new VanillaAnalyticsBuilder(MEASUREMENT_ID, API_SECRET);
    }
}
