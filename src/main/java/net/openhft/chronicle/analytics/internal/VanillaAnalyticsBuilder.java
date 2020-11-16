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
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class VanillaAnalyticsBuilder implements Analytics.Builder, AnalyticsConfiguration {

    private boolean built;
    private final String measurementId;
    private final String apiSecret;
    //
    private final Map<String, String> userProperties = new LinkedHashMap<>();
    private final Map<String, String> eventParameters = new LinkedHashMap<>();
    private Consumer<String> errorLogger = System.err::println;
    private Consumer<String> debugLogger = s -> {
    };
    private long duration;
    private TimeUnit timeUnit = TimeUnit.SECONDS;
    private String clientIdFileName = Optional.ofNullable(System.getProperty("user.home")).orElse(".") + "/chronicle.analytics.client.id";
    private String url = "https://www.google-analytics.com/mp/collect";
    private boolean reportDespiteJUnit;

    public VanillaAnalyticsBuilder(@NotNull final String measurementId, @NotNull final String apiSecret) {
        this.measurementId = measurementId;
        this.apiSecret = apiSecret;
    }

    @NotNull
    @Override
    public Analytics.Builder putUserProperty(@NotNull final String key, @NotNull final String value) {
        userProperties.put(key, value);
        return this;
    }

    @NotNull
    @Override
    public Analytics.Builder putEventParameter(@NotNull final String key, @NotNull final String value) {
        eventParameters.put(key, value);
        return this;
    }

    @NotNull
    @Override
    public Analytics.Builder withFrequencyLimit(final long duration, @NotNull final TimeUnit timeUnit) {
        if (duration < 0) {
            throw new IllegalArgumentException("duration must not be negative, was " + duration);
        }
        this.duration = duration;
        this.timeUnit = timeUnit;
        return this;
    }

    @Override
    public Analytics.@NotNull Builder withErrorLogger(@NotNull final Consumer<String> errorLogger) {
        this.errorLogger = errorLogger;
        return this;
    }

    @Override
    public Analytics.@NotNull Builder withDebugLogger(@NotNull final Consumer<String> debugLogger) {
        this.debugLogger = debugLogger;
        return this;
    }

    @Override
    public Analytics.@NotNull Builder withClientIdFileName(@NotNull final String clientIdFileName) {
        this.clientIdFileName = clientIdFileName;
        return this;
    }

    @Override
    public Analytics.@NotNull Builder withUrl(@NotNull final String url) {
        this.url = url;
        return this;
    }

    @Override
    public Analytics.@NotNull Builder withReportDespiteJUnit() {
        this.reportDespiteJUnit = true;
        return this;
    }

    @NotNull
    @Override
    public Analytics build() {
        if (built)
            // This protects from modifying the builder after it has been used to build a new object.
            throw new IllegalStateException("This builder has already been used.");
        built = true;

        if (JUnitUtil.isJUnitAvailable() && !reportDespiteJUnit)
            return new MuteAnalytics();
        else
            return new GoogleAnalytics(this);
    }

    // Accessors

    @Override
    public @NotNull String measurementId() {
        return measurementId;
    }

    @Override
    public @NotNull String apiSecret() {
        return apiSecret;
    }

    @Override
    public @NotNull Map<String, String> userProperties() {
        // ok because the builder cannot be reused
        return userProperties;
    }

    @Override
    public @NotNull Map<String, String> eventParameters() {
        // ok because the builder cannot be reused
        return eventParameters;
    }

    @Override
    public @NotNull Consumer<String> errorLogger() {
        return errorLogger;
    }

    @Override
    public @NotNull Consumer<String> debugLogger() {
        return debugLogger;
    }

    @Override
    public long duration() {
        return duration;
    }

    @Override
    public @NotNull TimeUnit timeUnit() {
        return timeUnit;
    }

    @Override
    public @NotNull String clientIdFileName() {
        return clientIdFileName;
    }

    @Override
    public @NotNull String url() {
        return url;
    }
}