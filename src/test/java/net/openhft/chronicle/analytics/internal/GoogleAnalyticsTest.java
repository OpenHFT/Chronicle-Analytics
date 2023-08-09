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

import org.jetbrains.annotations.NotNull;
import org.junit.Assume;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static net.openhft.chronicle.analytics.internal.FilesUtil.removeLastUsedFileTimeStampSecond;
import static org.junit.jupiter.api.Assertions.*;

//@Disabled(/* failing test https://teamcity.chronicle.software/buildConfiguration/Chronicle_BuildAll_Build/677499?hideProblemsFromDependencies=false&hideTestsFromDependencies=false&expandBuildChangesSection=true&expandBuildTestsSection=true */)
final class GoogleAnalyticsTest {

    @Test
    void jsonFor() {
        final String expected = "{\n" +
                " 'clientId': '123',\n" +
                " 'userId': '123',\n" +
                " 'nonPersonalizedAds': true,\n" +
                " 'events': [{\n" +
                "  'name': 'started',\n" +
                "  'params': {\n" +
                "   'A': '1',\n" +
                "   'B': '2'\n" +
                "  }\n" +
                " }],\n" +
                " 'userProperties': {\n" +
                "  'C': {\n" +
                "    'value': '3'\n" +
                "  },\n" +
                "  'D': {\n" +
                "    'value': '4'\n" +
                "  }\n" +
                " }\n" +
                "}";

        final Map<String, String> eventParameters = testMap(0);
        final Map<String, String> userProperties = testMap(2);
        final String actual = GoogleAnalytics4.jsonFor("started", "123", eventParameters, userProperties);

        assertEquals(expected, actual.replace('"', '\'')
                .replace("\r\n", "\n"));
    }

    @Test
    void renderMap() {
        final Map<String, String> map = new LinkedHashMap<>();
        map.put("A", "1");
        map.put("B", "2");
        map.put("C", "4"); // And why 4? I guess we'll never know...

        final Function<Map.Entry<String, String>, String> mapper = e -> String.format("mapKey_%s:mapValue_%s", e.getKey(), e.getValue());

        final String expected = String.format("mapKey_A:mapValue_1,%nmapKey_B:mapValue_2,%nmapKey_C:mapValue_4");
        final String actual = GoogleAnalytics4.renderMap(map, mapper);

        assertEquals(expected, actual);
    }

    @Test
    void attemptToSendOneShot() {
        removeLastUsedFileTimeStampSecond();
        GoogleAnalytics4 googleAnalytics = (GoogleAnalytics4) new VanillaAnalyticsBuilder("", "")
                .withFrequencyLimit(2, 1, TimeUnit.HOURS)
                .withReportDespiteJUnit()
                .build();

        assertTrue(googleAnalytics.attemptToSend());
        assertTrue(googleAnalytics.attemptToSend());
        assertFalse(googleAnalytics.attemptToSend());
    }

    @Test
    void attemptToSendReset() {
        final int messages = 5;
        final TimeUnit timeUnit = TimeUnit.SECONDS;
        final long duration = 1;
        removeLastUsedFileTimeStampSecond();
        final GoogleAnalytics4 googleAnalytics = (GoogleAnalytics4) new VanillaAnalyticsBuilder("", "")
                .withFrequencyLimit(messages, duration, timeUnit)
                .withReportDespiteJUnit()
                .build();

        /* This test would fail if run concurrently from two different JVMs,
        therefore it's only run if the googleAnalytics instance is not muted (update in agreement with Rob)
        */
        Assume.assumeFalse(googleAnalytics.muted);
        for (int i = 0; i < messages; i++) {
            assertTrue(googleAnalytics.attemptToSend(), "Round " + i);
        }
        assertFalse(googleAnalytics.attemptToSend());
        try {
            Thread.sleep(timeUnit.toMillis(duration) + 100);
        } catch (InterruptedException ignored) {
            // Should not happen
        }
        // Hurray! We've got more messages!
        for (int i = 0; i < messages; i++) {
            assertTrue(googleAnalytics.attemptToSend());
        }
        assertFalse(googleAnalytics.attemptToSend());
    }

    @Test
    void attemptToSendLastUsedLimit() {
        removeLastUsedFileTimeStampSecond();

        for (int i = 0; i < 2; i++) {
            waitForNewSecond();
            GoogleAnalytics4 googleAnalytics = (GoogleAnalytics4) new VanillaAnalyticsBuilder("", "")
                    .withFrequencyLimit(1, 1, TimeUnit.HOURS)
                    .withReportDespiteJUnit()
                    .build();

            // It is now likely that this instance is creates on the same second of the day
            GoogleAnalytics4 googleAnalytics2 = (GoogleAnalytics4) new VanillaAnalyticsBuilder("", "")
                    .withFrequencyLimit(1, 1, TimeUnit.HOURS)
                    .withReportDespiteJUnit()
                    .build();

            assertTrue(googleAnalytics.attemptToSend());
            // Because googleAnalytics2 was created on the same second as the previous,
            // no send should be made
            assertFalse(googleAnalytics2.attemptToSend());

        }
    }

    private void waitForNewSecond() {
        final int lastSecond = LocalTime.now().toSecondOfDay();
        // Wait for a fresh second
        while (LocalTime.now().toSecondOfDay() == lastSecond) {
            // spin wait
        }
    }

    private Map<String, String> testMap(int start) {
        final Map<String, String> map = new LinkedHashMap<>();
        map.put(asChar('A', start), asChar('1', start));
        map.put(asChar('B', start), asChar('2', start));
        return map;
    }

    @NotNull
    private String asChar(char base, int offset) {
        base += offset;
        return "" + base;
    }
}