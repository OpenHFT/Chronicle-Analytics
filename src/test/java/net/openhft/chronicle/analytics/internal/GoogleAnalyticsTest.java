package net.openhft.chronicle.analytics.internal;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

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

        assertEquals(expected, actual.replace('"', '\''));
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

        final GoogleAnalytics4 googleAnalytics = (GoogleAnalytics4) new VanillaAnalyticsBuilder("", "")
                .withFrequencyLimit(messages, duration, timeUnit)
                .withReportDespiteJUnit()
                .build();

        for (int i = 0; i < messages; i++) {
            assertTrue(googleAnalytics.attemptToSend());
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