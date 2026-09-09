/*
 * Copyright 2013-2025 chronicle.software; SPDX-License-Identifier: Apache-2.0
 */
package net.openhft.chronicle.analytics.internal;

import net.openhft.chronicle.analytics.Analytics;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Default {@link Analytics.Builder} and {@link AnalyticsConfiguration} implementation.
 *
 * <p>This builder collects measurement identifiers, credentials, user properties, event parameters,
 * and simple rate limiting settings before constructing an {@link Analytics} instance. Depending on
 * the configured measurement identifier it will create either a {@link GoogleAnalytics3} or
 * {@link GoogleAnalytics4} implementation, or a {@link MuteAnalytics} instance when tests are
 * detected via {@link JUnitUtil}.
 *
 * <p>The builder is single use; once {@link #build()} has been called further modifications will
 * result in an {@link IllegalStateException}.
 */
public final class VanillaAnalyticsBuilder implements Analytics.Builder, AnalyticsConfiguration {

    private boolean built;
    private final String measurementId;
    private final String apiSecret;
    //
    private final Map<String, String> userProperties = new LinkedHashMap<>();
    private final Map<String, String> eventParameters = new LinkedHashMap<>();
    private Consumer<? super String> errorLogger = System.err::println;
    private Consumer<? super String> debugLogger = s -> {
    };
    private long duration;
    private TimeUnit timeUnit = TimeUnit.SECONDS;
    private int messages = 0;
    private String clientIdFileName = Optional.ofNullable(System.getProperty("user.home")).orElse(".") + "/.chronicle.analytics.client.id";
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
    public Analytics.Builder withFrequencyLimit(int messages, final long duration, @NotNull final TimeUnit timeUnit) {
        if (messages < 0) {
            throw new IllegalArgumentException("messages must not be negative, was " + messages);
        }
        if (duration < 0) {
            throw new IllegalArgumentException("duration must not be negative, was " + duration);
        }
        this.messages = messages;
        this.duration = duration;
        this.timeUnit = timeUnit;
        return this;
    }

    @Override
    public Analytics.@NotNull Builder withErrorLogger(@NotNull final Consumer<? super String> errorLogger) {
        this.errorLogger = errorLogger;
        return this;
    }

    @Override
    public Analytics.@NotNull Builder withDebugLogger(@NotNull final Consumer<? super String> debugLogger) {
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
            return MuteAnalytics.INSTANCE;
        else
            if (measurementId.startsWith("UA-")) {
                return new GoogleAnalytics3(this);
            } else {
                return new GoogleAnalytics4(this);
            }
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

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull Consumer<String> errorLogger() {
        return (Consumer<String>) errorLogger;
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull Consumer<String> debugLogger() {
        return (Consumer<String>) debugLogger;
    }

    @Override
    public int messages() {
        return messages;
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
