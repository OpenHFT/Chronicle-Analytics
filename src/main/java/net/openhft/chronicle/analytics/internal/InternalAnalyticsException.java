package net.openhft.chronicle.analytics.internal;

@SuppressWarnings("serial")
final class InternalAnalyticsException extends RuntimeException {

    InternalAnalyticsException(String message) {
        super(message);
    }
}