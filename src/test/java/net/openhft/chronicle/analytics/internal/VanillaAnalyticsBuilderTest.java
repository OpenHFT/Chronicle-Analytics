/*
 * Copyright 2016-2022 chronicle.software
 *
 *       https://chronicle.software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
                builder.withFrequencyLimit(1, -1, TimeUnit.SECONDS)
        );
    }

    @Test
    void withFrequencyLimit2() {
        assertNotNull(newInstance().withFrequencyLimit(1, 1, TimeUnit.SECONDS));
    }

    @Test
    void withErrorLogger() {
        assertSame(TEST_LOGGER, ((VanillaAnalyticsBuilder) newInstance().withErrorLogger(TEST_LOGGER)).errorLogger());
    }

    @Test
    void withErrorLoggerSuper() {
        final AtomicReference<CharSequence> reference = new AtomicReference<>();
        final Consumer<? super String> consumer = (Consumer<CharSequence>) reference::set;

        final Consumer<String> returnedConsumer = ((VanillaAnalyticsBuilder) newInstance().withErrorLogger(consumer)).errorLogger();

        final String s = "Tryggve";

        returnedConsumer.accept(s);
        assertEquals(s, reference.get());
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
    void withReportDespiteJUnit() {
        assertTrue(newInstance().withReportDespiteJUnit().build() instanceof GoogleAnalytics4);
    }

    @Test
    void withReportDespiteJUnit2() {
        final Analytics analytics = newInstance().build();
        assertTrue(newInstance().build() instanceof MuteAnalytics);
    }

    @Test
    void withUrl() {
        assertEquals(TEST_STRING0, ((VanillaAnalyticsBuilder) newInstance().withUrl(TEST_STRING0)).url());
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
        final String defaultFileName = System.getProperty("user.home") + "/.chronicle.analytics.client.id";
        assertEquals(defaultFileName, newInstance().clientIdFileName());
    }

    @Test
    void url() {
        final String defaultUrl = "https://www.google-analytics.com/mp/collect";
        assertEquals(defaultUrl, newInstance().url());
    }

    private VanillaAnalyticsBuilder newInstance() {
        return new VanillaAnalyticsBuilder(MEASUREMENT_ID, API_SECRET);
    }
}