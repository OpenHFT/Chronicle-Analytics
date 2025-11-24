/*
 * Copyright 2013-2025 chronicle.software; SPDX-License-Identifier: Apache-2.0
 */
package net.openhft.chronicle.analytics.internal;

/**
 * Minimal JSON rendering helpers used by the analytics implementations.
 *
 * <p>This utility focuses on the small subset of JSON needed to construct Google Analytics
 * payloads. It provides simple escaping and element rendering rather than a full JSON library.
 */
final class JsonUtil {

    private static final String NL = String.format("%n");

    private JsonUtil() {
    }

    static String jsonElement(final String indent,
                              final String key,
                              final Object value) {
        return indent + asElement(key) + ": " + asElement(value);
    }

    static String asElement(final Object value) {
        return value instanceof CharSequence
                ? '"' + escape(value.toString()) + '"'
                : value.toString();

    }

    static String escape(final String raw) {
        return raw
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
        // Todo: escape other non-printing characters ...
    }

    static String nl() {
        return NL;
    }
}
