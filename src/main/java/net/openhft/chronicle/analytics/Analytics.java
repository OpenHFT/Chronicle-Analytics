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
package net.openhft.chronicle.analytics;

import net.openhft.chronicle.analytics.internal.VanillaAnalyticsBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Provides means for libraries to report analytics to an upstream receiver.
 * <p>
 * Analytics instances only provides a best-effort to propagate
 * events to the upstream receiver.
 * <p>
 * Analytics can be turned off buy setting the system property
 * "chronicle.analytics.disable=true" prior to acquiring any Analytics
 * instances.
 */
public interface Analytics {

    /**
     * Sends an event to Google Analytics as identified by the provided event {@code name)}.
     * <p>
     * Depending on settings and other conditions, the event may or may not be
     * sent upstream. For example, some implementations may
     * send a limited number of upstream events per time unit.
     */
    default void sendEvent(@NotNull String name) {
        sendEvent(name, Collections.emptyMap());
    }

    /**
     * Sends an event to Google Analytics as identified by the provided event {@code name)} including
     * the provided {@code additionalEventParameters} in the event.
     * <p>
     * Depending on settings and other conditions, the event may or may not be
     * sent upstream. For example, some implementations may
     * send a limited number of upstream events per time unit.
     */
    void sendEvent(@NotNull String name, @NotNull Map<String, String> additionalEventParameters);

    @NotNull
    static Builder builder(@NotNull final String measurementId, @NotNull final String apiSecret) {
        return new VanillaAnalyticsBuilder(measurementId, apiSecret);
    }

    interface Builder {

        /**
         * Associates the provided {@code value} with the provided {@code key} in this builder's user properties.
         * If the builder previously contained an association for the key, the old value is replaced
         * by the provided value.
         * <p>
         * The key will be used as a Google Analytics "user property" key with the
         * associated value.
         * </p>
         *
         * @param key   to associate
         * @param value to associate with the key
         * @return this builder
         */
        @NotNull
        Builder putUserProperty(@NotNull String key, @NotNull String value);

        /**
         * Associates the provided {@code value} with the provided {@code key} in this builder's event parameters.
         * If the builder previously contained an association for the key, the old value is replaced
         * by the provided value.
         * <p>
         * The key will be used as a Google Analytics "event parameter" key with the
         * associated value.
         * </p>
         *
         * @param key   to associate
         * @param value to associate with the key
         * @return this builder
         */
        @NotNull
        Builder putEventParameter(@NotNull String key, @NotNull String value);

        /**
         * Limits the frequency by which events can be sent upstream to Google Analytics.
         * <p>
         * Events that are posted within the provided time limit, counted from the time the last
         * message was successfully attempted upstream, are silently dropped.
         * <p>
         * Thus, the highest rate of messages sent upstream can be calculated using the
         * following function {@code
         * <p>
         * 1.0d/timeUnit.toSeconds(duration)
         * <p>
         * }
         * which yields messages per second.
         *
         * @param duration minimum duration between upstream messages.
         * @param timeUnit for the provided duration.
         * @return this builder
         */
        @NotNull
        Builder withFrequencyLimit(long duration, @NotNull TimeUnit timeUnit);


        /**
         * Specifies a custom logger that will receive error messages.
         * For example, failed HTTP communications.
         * <p>
         * The default logger is {@link System#err::println}, i.e. messages will be
         * output to the error console.
         * <p>
         * The actual messages sent to the error logger are unspecified
         * and shall not be acted on by logic.
         *
         * @param errorLogger to use for error messages.
         * @return this builder
         */
        Builder withErrorLogger(@NotNull Consumer<String> errorLogger);

        /**
         * Specifies a custom logger that will receive debug messages.
         * For example, HTTP messages sent back and forth.
         * <p>
         * The default logger is {@code s -> {}}, i.e. messages will be
         * discarded.
         * <p>
         * The actual messages sent to the debug logger are unspecified
         * and shall not be acted on by logic.
         *
         * @param debugLogger to use for debug messages.
         * @return this builder
         */
        Builder withDebugLogger(@NotNull Consumer<String> debugLogger);

        /**
         * Creates and returns a new Analytics instance for this Builder.
         *
         * @return a new Analytics instance for this Builder
         */
        @NotNull
        Analytics build();
    }

}