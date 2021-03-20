/*
 * Copyright 2016-2020 chronicle.software
 *
 * https://chronicle.software
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
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static net.openhft.chronicle.analytics.internal.JsonUtil.asElement;
import static net.openhft.chronicle.analytics.internal.JsonUtil.jsonElement;

final class GoogleAnalytics4 extends AbstractGoogleAnalytics implements Analytics {

    GoogleAnalytics4(@NotNull final AnalyticsConfiguration configuration) {
        super(configuration);
    }

    void httpSend(@NotNull String eventName, @NotNull final Map<String, String> eventParameters) {
        final String url = configuration().url() + "?measurement_id=" + urlEncode(configuration().measurementId()) + "&api_secret=" + urlEncode(configuration().apiSecret());
        final String json = jsonFor(eventName, clientId(), eventParameters, configuration().userProperties());
        HttpUtil.send(url, json, configuration().errorLogger(), configuration().debugLogger());
    }

    static String jsonFor(@NotNull final String eventName,
                          @NotNull final String clientId,
                          @NotNull final Map<String, String> eventParameters,
                          @NotNull final Map<String, String> userProperties) {
        return Stream.of(
                "{",
                jsonElement(" ", "clientId", clientId) + ',',
                jsonElement(" ", "userId", clientId) + ',',
                jsonElement(" ", "nonPersonalizedAds", true) + ',',
                ' ' + asElement("events") + ": [{",
                jsonElement("  ", "name", eventName) + ',',
                "  " + asElement("params") + ": {",
                renderMap(eventParameters, e -> jsonElement("   ", e.getKey(), e.getValue())),
                "  }",
                " }],",
                ' ' + asElement("userProperties") + ": {",
                renderMap(userProperties, GoogleAnalytics4::userProperty),
                " }",
                "}"
        ).collect(joining(JsonUtil.nl()));
    }

    static String userProperty(final Map.Entry<String, String> userProperty) {
        return String.format("  %s: {%n %s%n  }", asElement(userProperty.getKey()), jsonElement("   ", "value", userProperty.getValue()));
    }

    static String renderMap(@NotNull final Map<String, String> map, @NotNull final Function<Map.Entry<String, String>, String> mapper) {
        return map.entrySet().stream()
                .map(mapper)
                .collect(joining(String.format(",%n")));
    }
}