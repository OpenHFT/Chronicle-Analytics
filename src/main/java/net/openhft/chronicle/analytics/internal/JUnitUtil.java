/*
 * Copyright 2013-2025 chronicle.software; SPDX-License-Identifier: Apache-2.0
 */
package net.openhft.chronicle.analytics.internal;

import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

/**
 * Utility for detecting whether JUnit is present on the classpath.
 *
 * <p>The analytics builder uses this to disable reporting when tests are running, unless the
 * caller explicitly opts in via configuration.
 */
final class JUnitUtil {

    private JUnitUtil() {}

    static boolean isJUnitAvailable() {
        return Stream.of("org.junit.jupiter.api.Test", "org.junit.Test")
                .anyMatch(JUnitUtil::isClassAvailable);
    }

    static boolean isClassAvailable(@NotNull final String className) {
        try {
            Class.forName(className);
        } catch (ClassNotFoundException ignore) {
            return false;
        }
        return true;
    }
}
