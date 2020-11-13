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
import java.util.concurrent.TimeUnit;

public final class VanillaAnalyticsBuilder implements Analytics.Builder {

    private final String measurementId;
    private final String apiSecret;
    private final Map<String, String> userProperties = new LinkedHashMap<>();
    private final Map<String, String> eventParameters = new LinkedHashMap<>();
    private long duration;
    private TimeUnit timeUnit;

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
        this.duration = duration;
        this.timeUnit = timeUnit;
        return this;
    }

    @NotNull
    @Override
    public Analytics build() {
        return null;
    }

}