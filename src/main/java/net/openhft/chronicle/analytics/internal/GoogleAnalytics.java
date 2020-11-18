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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static net.openhft.chronicle.analytics.internal.HttpUtil.urlEncode;
import static net.openhft.chronicle.analytics.internal.JsonUtil.asElement;
import static net.openhft.chronicle.analytics.internal.JsonUtil.jsonElement;

final class GoogleAnalytics implements Analytics {

    private final AnalyticsConfiguration configuration;
    private final String clientId;
    private final AtomicLong lastSendAttemptNs = new AtomicLong();

    GoogleAnalytics(@NotNull final AnalyticsConfiguration configuration) {
        this.configuration = configuration;
        this.clientId = ClientIdUtil.acquireClientId(configuration.clientIdFileName(), configuration.debugLogger());
    }

    @Override
    public void sendEvent(@NotNull final String name, @NotNull final Map<String, String> additionalEventParameters) {
        if (configuration.duration() > 0) {
            final long nextThresholdNs = lastSendAttemptNs.get() + configuration.timeUnit().toNanos(configuration.duration());
            if (System.nanoTime() < nextThresholdNs)
                // Drop this send event because it was too
                // close to the previous send attempt not dropped
                return;
        }
        lastSendAttemptNs.set(System.nanoTime());

        if (additionalEventParameters.isEmpty()) {
            httpSend(name, configuration.eventParameters());
        } else {
            final Map<String, String> mergedEventParameters = new LinkedHashMap<>(configuration.eventParameters());
            mergedEventParameters.putAll(additionalEventParameters);
            httpSend(name, mergedEventParameters);
        }
    }

    private void httpSend(@NotNull String eventName, @NotNull final Map<String, String> eventParameters) {
        final String url = configuration.url() + "?measurement_id=" + urlEncode(configuration.measurementId(), configuration.errorLogger()) + "&api_secret=" + urlEncode(configuration.apiSecret(), configuration.errorLogger());
        final String json = jsonFor(eventName, clientId, eventParameters, configuration.userProperties());
        HttpUtil.send(url, json, configuration.errorLogger(), configuration.debugLogger());
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
                renderMap(userProperties, GoogleAnalytics::userProperty),
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