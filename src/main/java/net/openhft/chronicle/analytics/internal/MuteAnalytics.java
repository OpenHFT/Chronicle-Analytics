/*
 * Copyright 2013-2025 chronicle.software; SPDX-License-Identifier: Apache-2.0
 */
package net.openhft.chronicle.analytics.internal;

import net.openhft.chronicle.analytics.Analytics;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * {@link Analytics} implementation that discards all events.
 *
 * <p>Used when analytics should be disabled, for example when tests are running or configuration
 * is incomplete. Events are counted locally via {@link #mutedEvents} for diagnostic purposes but
 * never sent over the network.
 */
enum MuteAnalytics implements Analytics {
    INSTANCE;
    int mutedEvents = 0;

    @Override
    public void sendEvent(@NotNull String name, @NotNull Map<String, String> additionalEventParameters) {
        // Do nothing because this is a mute instance.
        mutedEvents++;
    }
}
