/*
 * Copyright 2013-2026 chronicle.software; SPDX-License-Identifier: Apache-2.0
 */
package net.openhft.chronicle.analytics.internal;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public interface AnalyticsConfiguration {
    @NotNull String measurementId();
    @NotNull String apiSecret();
    @NotNull Map<String, String> userProperties();
    @NotNull Map<String, String> eventParameters();
    @NotNull Consumer<String> errorLogger();
    @NotNull Consumer<String> debugLogger();
    long duration();
    int messages();
    @NotNull TimeUnit timeUnit();
    @NotNull String clientIdFileName();
    @NotNull String url();
}
