/*
 * Copyright 2016-2020 chronicle.software
 *
 *       https://chronicle.software
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