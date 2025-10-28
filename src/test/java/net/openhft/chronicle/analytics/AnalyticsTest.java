/*
 * Copyright 2016-2025 chronicle.software
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

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class AnalyticsTest {

    private static final String TEST_STRING = "Harry";

    @Test
    void sendEvent() {
        final AtomicReference<String> sendName = new AtomicReference<>();
        final AtomicReference<Map<String, String>> sendParameters = new AtomicReference<>();
        final Analytics analytics = (name, additionalEventParameters) -> {
            sendName.set(name);
            sendParameters.set(additionalEventParameters);
        };

        analytics.sendEvent(TEST_STRING);
        assertEquals(TEST_STRING, sendName.get());
        assertEquals(Collections.emptyMap(), sendParameters.get());
    }

    @Test
    void sendEventWithAdditionalParameters() {
        final Map<String, String> parameters = Collections.singletonMap("key", "value");
        final AtomicReference<Map<String, String>> capturedParameters = new AtomicReference<>();
        final Analytics analytics = (name, additionalEventParameters) -> capturedParameters.set(additionalEventParameters);

        analytics.sendEvent(TEST_STRING, parameters);
        assertSame(parameters, capturedParameters.get());
    }

    @Test
    void builder() {
        Analytics.Builder builder = Analytics.builder(TEST_STRING, TEST_STRING);
        assertNotNull(builder);
    }
}
