/*
 * Copyright 2013-2025 chronicle.software; SPDX-License-Identifier: Apache-2.0
 */
package net.openhft.chronicle.analytics;

public class AnalyticsExampleMain {

    public static void main(String[] args) {

        Analytics analytics = Analytics.builder("G-TDAZG4CU3G", "k2hL3x2dQaKq9F2gQ-PNhQ")
                .putEventParameter("app_version", "1.4.2")
                .putUserProperty("os_name", System.getProperty("os.name"))
                .putUserProperty("os_version", System.getProperty("os.version"))
                .putUserProperty("java_runtime_version", System.getProperty("java.runtime.version"))
                .build();

        analytics.sendEvent("started");

        // do some job

        analytics.sendEvent("completed");

        AnalyticsExampleMain exampleMain = new AnalyticsExampleMain();
        exampleMain.showBuilder();
        exampleMain.shortExample();
    }

    private void showBuilder() {
        String measurementId = "G-TDAZG4CU3G";
        String apiSecret = "k2hL3x2dQaKq9F2gQ-PNhQ";
        Analytics.Builder builder = Analytics.builder(measurementId, apiSecret);

        // optionally configure the builder, see JavaDocs

        Analytics analytics = builder.build();
        analytics.sendEvent("started");

    }

    private void shortExample() {
        Analytics.builder("G-TDAZG4CU3G", "k2hL3x2dQaKq9F2gQ-PNhQ")
                .putEventParameter("app_version", "1.4.2")
                .build()
                .sendEvent("started");
    }
}
