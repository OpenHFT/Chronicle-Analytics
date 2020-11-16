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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

final class GoogleAnalytics implements Analytics {

    private static final String ENDPOINT_URL = "https://www.google-analytics.com/mp/collect";

    private final AnalyticsConfiguration configuration;
    private final String clientId;

    GoogleAnalytics(AnalyticsConfiguration configuration) {
        this.configuration = configuration;
        this.clientId = acquireClientId();
    }

    @Override
    public void sendEvent(@NotNull final String name, @NotNull final Map<String, String> additionalEventParameters) {
        if (additionalEventParameters.isEmpty()) {
            httpSend(name, configuration.eventParameters());
        } else {
            final Map<String, String> mergedEventParameters = new LinkedHashMap<>(configuration.eventParameters());
            mergedEventParameters.putAll(additionalEventParameters);
            httpSend(name, mergedEventParameters);
        }
    }

    private void httpSend(@NotNull String eventName, @NotNull final Map<String, String> eventParameters) {
        final String url = ENDPOINT_URL + "?measurement_id=" + urlEncode(configuration.measurementId()) + "&api_secret=" + urlEncode(configuration.apiSecret());
        final String json = jsonFor(eventName, eventParameters, configuration.userProperties());
        HttpUtil.send(url, json, configuration.errorLogger(), configuration.debugLogger());
    }

    private String jsonFor(@NotNull final String eventName,
                           @NotNull final Map<String, String> eventParameters,
                           @NotNull final Map<String, String> userProperties) {
        return Stream.of(
                "{",
                jsonElement(" ", "clientId", clientId) + ",",
                jsonElement(" ", "userId", clientId) + ",",
                jsonElement(" ", "nonPersonalizedAds", true) + ",",
                " " + asElement("events") + ": [{",
                jsonElement("  ", "name", eventName) + ",",
                "  " + asElement("params") + ": {",
                renderMap(eventParameters, e -> jsonElement("   ", e.getKey(), e.getValue())),
                "  }",
                " }],",
                " " + asElement("userProperties") + ": {",
                renderMap(userProperties, this::userProperty),
                " }",
                "}"
        ).collect(joining(nl()));
    }

    private String jsonElement(final String indent,
                               final String key,
                               final Object value) {
        return indent + asElement(key) + ": " + asElement(value);
    }

    private String asElement(final Object value) {
        return value instanceof CharSequence
                ? "\"" + value + "\""
                : value.toString();

    }

    private String userProperty(final Map.Entry<String, String> userProperty) {
        return String.format("  %s: {%n %s%n  }", asElement(userProperty.getKey()), jsonElement("   ", "value", userProperty.getValue()));
    }

    private String urlEncode(final String s) {
        try {
            return URLEncoder.encode(s, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            configuration.errorLogger().accept(e.toString());
            throw new InternalAnalyticsException("This should never happen as " + StandardCharsets.UTF_8.toString() + " should always be present.");
        }
    }

    private String renderMap(@NotNull final Map<String, String> map, @NotNull final Function<Map.Entry<String, String>, String> mapper) {
        return map.entrySet().stream()
                .map(mapper)
                .collect(joining(String.format(",%n")));
    }

    // This tries to read a client id from a "cookie" file in the
    // user's home directory. If that fails, a new random clientId
    // is generated and an attempt is made to save it in said file.
    private String acquireClientId() {
        final Path path = Paths.get(configuration.clientIdFileName());
        try {
            try (Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8)) {
                return lines
                        .findFirst()
                        .map(UUID::fromString)
                        .orElseThrow(NoSuchElementException::new)
                        .toString();
            }
        } catch (Exception ignore) {
            configuration.debugLogger().accept("Client id file not present: " + path.toAbsolutePath().toString());
        }
        final String id = UUID.randomUUID().toString();
        try {
            Files.write(path, id.getBytes(StandardCharsets.UTF_8));
        } catch (IOException ignore) {
            configuration.debugLogger().accept("Unable to create client id file: " + path.toAbsolutePath().toString());
        }
        return id;
    }

    private String nl() {
        return String.format("%n");
    }

}