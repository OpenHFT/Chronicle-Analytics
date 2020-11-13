package net.openhft.chronicle.analytics;

public class AnalyticsExample {

    public static void main(String[] args) {

        Analytics analytics = Analytics.builder("G-TDAZG4CU3G", "k2hL3x2dQaKq9F2gQ-PNhQ")
                .putEventParameter("app_version", "1.4.2")
                .putUserProperty("os_name", System.getProperty("os.name"))
                .putUserProperty("java_runtime_version", System.getProperty("java.runtime.version"))
                .build();

        analytics.sendEvent("started");

        // do some job

        analytics.sendEvent("completed");

    }
}