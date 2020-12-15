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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

abstract class AbstractGoogleAnalytics implements Analytics {

    private final AnalyticsConfiguration configuration;
    private final String clientId;
    private final AtomicLong lastSendAttemptNs = new AtomicLong();
    private final AtomicInteger sentMessages = new AtomicInteger();

    AbstractGoogleAnalytics(@NotNull final AnalyticsConfiguration configuration) {
        this.configuration = configuration;
        this.clientId = ClientIdUtil.acquireClientId(configuration.clientIdFileName(), configuration.debugLogger());
    }

    @Override
    public void sendEvent(@NotNull final String name, @NotNull final Map<String, String> additionalEventParameters) {
        if (attemptToSend()) {
            if (additionalEventParameters.isEmpty()) {
                httpSend(name, configuration.eventParameters());
            } else {
                final Map<String, String> mergedEventParameters = new LinkedHashMap<>(configuration.eventParameters());
                mergedEventParameters.putAll(additionalEventParameters);
                httpSend(name, mergedEventParameters);
            }
        }
    }

    abstract void httpSend(@NotNull String eventName, @NotNull final Map<String, String> eventParameters);

    boolean attemptToSend() {
        if (configuration.duration() > 0) {
            final long nextThresholdNs = lastSendAttemptNs.get() + configuration.timeUnit().toNanos(configuration.duration());
            if (System.nanoTime() > nextThresholdNs || nextThresholdNs == 0) {
                // Reset
                lastSendAttemptNs.set(System.nanoTime());
                sentMessages.set(1);
            } else {
                if (sentMessages.getAndIncrement() >= configuration.messages()) {
                    // Drop this send event because we have exceeded
                    // the max number of messages per duration
                    return false;
                }
            }
        }
        return true;
    }

    AnalyticsConfiguration configuration() {
        return configuration;
    }

    String clientId() {
        return clientId;
    }

    String urlEncode(String s) {
        return HttpUtil.urlEncode(s, configuration().errorLogger());
    }

}