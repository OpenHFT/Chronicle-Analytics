/*
 * Copyright 2013-2026 chronicle.software; SPDX-License-Identifier: Apache-2.0
 */
package net.openhft.chronicle.analytics.internal;

import net.openhft.chronicle.analytics.Analytics;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;

final class GoogleAnalytics3 extends AbstractGoogleAnalytics implements Analytics {

    private static final String URL_STRING = "https://www.google-analytics.com/collect";

    GoogleAnalytics3(@NotNull final AnalyticsConfiguration configuration) {
        super(configuration);
    }

    void httpSend(@NotNull String eventName, @NotNull final Map<String, String> eventParameters) {
        final String body = bodyFor(eventName, clientId(), eventParameters, configuration().userProperties());
        HttpUtil.send(URL_STRING, body, configuration().errorLogger(), configuration().debugLogger());
    }

    private String bodyFor(@NotNull final String eventName,
                           @NotNull final String clientId,
                           @NotNull final Map<String, String> eventParameters,
                           @NotNull final Map<String, String> userProperties) {

        final String version = Optional.ofNullable(eventParameters.remove("app_version")).orElse("unknown");

        final StringJoiner payload = new StringJoiner("&")
                .add("v=" + urlEncode("1")) // version. See https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#v
                .add("ds=" + urlEncode("app")) // data source. See https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#ds
                .add("tid=" + urlEncode(configuration().measurementId())) //
                .add("cid=" + clientId)
                //.add("uip=" + encode(event.getIpAddress()))
                //.add("ua=" + encode(event.getUserAgent()))
                .add("t=" + urlEncode("screenview")) // Hit type
                .add("ni=" + urlEncode("1")) // None interactive flag
                .add("cd=" + urlEncode(eventName)) // Screen Name
                .add("an=" + urlEncode(configuration().apiSecret())) // Application Name
                .add("av=" + urlEncode(version)); // Application version

        /*
        eventType.sessionControl()
                .ifPresent(sc -> payload.add("sc=" + sc)); // Session control like "start" and "end" */

        final Map<String, String> combined = new LinkedHashMap<>(eventParameters);
        combined.putAll(userProperties);

        final AtomicInteger cnt = new AtomicInteger();
        combined.entrySet().stream()
                .limit(20)
                .map(e -> String.format("cd%d=%s", cnt.incrementAndGet(), e.getValue()))
                .forEach(payload::add);

        return payload.toString();

    }
}
