package net.openhft.chronicle.analytics.internal;

import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

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