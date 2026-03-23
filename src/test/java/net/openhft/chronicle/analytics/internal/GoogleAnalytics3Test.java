/*
 * Copyright 2013-2025 chronicle.software; SPDX-License-Identifier: Apache-2.0
 */
package net.openhft.chronicle.analytics.internal;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static net.openhft.chronicle.analytics.internal.FilesUtil.removeLastUsedFileTimeStampSecond;
import static org.junit.jupiter.api.Assertions.*;

final class GoogleAnalytics3Test {

    @TempDir
    Path tempDir;

    @Test
    void bodyForIncludesBuilderAndAdditionalParameters() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        removeLastUsedFileTimeStampSecond();

        final Map<String, String> builderEventParameters = new LinkedHashMap<>();
        builderEventParameters.put("app_version", "9.9.9");
        builderEventParameters.put("builderKey", "builderValue");

        final Map<String, String> userProperties = new LinkedHashMap<>();
        userProperties.put("userKey", "userValue");

        final TestAnalyticsConfiguration configuration = new TestAnalyticsConfiguration(
                "UA-TEST-123",
                "secret",
                userProperties,
                builderEventParameters,
                tempDir.resolve("client.id").toString()
        );

        final GoogleAnalytics3 analytics = new GoogleAnalytics3(configuration);

        final Map<String, String> merged = new LinkedHashMap<>(builderEventParameters);
        merged.put("extraKey", "extraValue");

        final Method bodyFor = GoogleAnalytics3.class.getDeclaredMethod(
                "bodyFor",
                String.class,
                String.class,
                Map.class,
                Map.class
        );
        bodyFor.setAccessible(true);

        final String payload = (String) bodyFor.invoke(analytics, "boot", "client-123", merged, userProperties);

        assertTrue(payload.contains("tid=UA-TEST-123"));
        assertTrue(payload.contains("cid=client-123"));
        assertTrue(payload.contains("cd=boot"));
        assertTrue(payload.contains("an=secret"));
        assertTrue(payload.contains("av=9.9.9"));
        assertTrue(payload.contains("cd1=builderValue"));
        assertTrue(payload.contains("cd2=extraValue"));
        assertTrue(payload.contains("cd3=userValue"));
        assertFalse(merged.containsKey("app_version"));
    }

    private static final class TestAnalyticsConfiguration implements AnalyticsConfiguration {

        private final String measurementId;
        private final String apiSecret;
        private final Map<String, String> userProperties;
        private final Map<String, String> eventParameters;
        private final String clientIdFileName;

        private TestAnalyticsConfiguration(@NotNull final String measurementId,
                                           @NotNull final String apiSecret,
                                           @NotNull final Map<String, String> userProperties,
                                           @NotNull final Map<String, String> eventParameters,
                                           @NotNull final String clientIdFileName) {
            this.measurementId = measurementId;
            this.apiSecret = apiSecret;
            this.userProperties = userProperties;
            this.eventParameters = eventParameters;
            this.clientIdFileName = clientIdFileName;
        }

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
            return userProperties;
        }

        @Override
        public @NotNull Map<String, String> eventParameters() {
            return eventParameters;
        }

        @Override
        public @NotNull Consumer<String> errorLogger() {
            return s -> {
            };
        }

        @Override
        public @NotNull Consumer<String> debugLogger() {
            return s -> {
            };
        }

        @Override
        public long duration() {
            return 0;
        }

        @Override
        public int messages() {
            return 0;
        }

        @Override
        public @NotNull TimeUnit timeUnit() {
            return TimeUnit.SECONDS;
        }

        @Override
        public @NotNull String clientIdFileName() {
            return clientIdFileName;
        }

        @Override
        public @NotNull String url() {
            return "https://example.invalid";
        }
    }
}
