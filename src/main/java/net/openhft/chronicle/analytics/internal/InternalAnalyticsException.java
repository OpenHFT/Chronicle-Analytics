//
// Copyright 2013-2025 chronicle.software; SPDX-License-Identifier: Apache-2.0
//

package net.openhft.chronicle.analytics.internal;

@SuppressWarnings("serial")
final class InternalAnalyticsException extends RuntimeException {
    private static final long serialVersionUID = 0L;

    InternalAnalyticsException(String message) {
        super(message);
    }
}
