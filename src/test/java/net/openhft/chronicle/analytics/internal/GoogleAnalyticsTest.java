package net.openhft.chronicle.analytics.internal;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

final class GoogleAnalyticsTest {

    @Test
    void jsonFor() {
        final String expected = "{\n" +
                " \"clientId\": \"123\",\n" +
                " \"userId\": \"123\",\n" +
                " \"nonPersonalizedAds\": true,\n" +
                " \"events\": [{\n" +
                "  \"name\": \"started\",\n" +
                "  \"params\": {\n" +
                "   \"A\": \"1\",\n" +
                "   \"B\": \"2\"\n" +
                "  }\n" +
                " }],\n" +
                " \"userProperties\": {\n" +
                "  \"C\": {\n" +
                "    \"value\": \"3\"\n" +
                "  },\n" +
                "  \"D\": {\n" +
                "    \"value\": \"4\"\n" +
                "  }\n" +
                " }\n" +
                "}";

        final Map<String, String> eventParameters = testMap(0);
        final Map<String, String> userProperties = testMap(2);
        final String actual = GoogleAnalytics.jsonFor("started", "123", eventParameters, userProperties);

        assertEquals(expected, actual);
    }

    @Test
    void renderMap() {
        final Map<String, String> map = new LinkedHashMap<>();
        map.put("A", "1");
        map.put("B", "2");
        map.put("C", "4"); // And why 4? I guess we'll never know...

        final Function<Map.Entry<String, String>, String> mapper = e -> String.format("mapKey_%s:mapValue_%s", e.getKey(), e.getValue());

        final String expected = String.format("mapKey_A:mapValue_1,%nmapKey_B:mapValue_2,%nmapKey_C:mapValue_4");
        final String actual = GoogleAnalytics.renderMap(map, mapper);

        assertEquals(expected, actual);
    }

    private Map<String, String> testMap(int start) {
        final Map<String, String> map = new LinkedHashMap<>();
        map.put(Character.toString((char) ('A' + start)), Character.toString((char) ('1' + start)));
        map.put(Character.toString((char) ('B' + start)), Character.toString((char) ('2' + start)));
        return map;
    }

}