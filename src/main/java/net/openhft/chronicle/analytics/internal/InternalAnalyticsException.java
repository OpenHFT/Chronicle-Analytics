/*
 * Copyright 2013-2025 chronicle.software; SPDX-License-Identifier: Apache-2.0
 */
package net.openhft.chronicle.analytics.internal;

/**
 * Runtime exception used to signal unexpected failures inside the analytics subsystem.
 *
 * <p>This exception is not intended to cross module boundaries; callers should normally catch it,
 * log via the configured error logger, and continue without reporting analytics.
 */
@SuppressWarnings("serial")
final class InternalAnalyticsException extends RuntimeException {
    private static final long serialVersionUID = 0L;

    InternalAnalyticsException(String message) {
        super(message);
    }
}
