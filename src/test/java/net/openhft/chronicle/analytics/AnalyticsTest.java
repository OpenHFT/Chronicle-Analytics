/*
 * Copyright 2016-2022 chronicle.software
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

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AnalyticsTest {

    private static final String TEST_STRING = "Harry";

    @Test
    void sendEvent() {
        final AtomicReference<String> sendName = new AtomicReference<>();
        final Analytics analytics = (name, additionalEventParameters) -> sendName.set(name);

        analytics.sendEvent(TEST_STRING);
        assertEquals(TEST_STRING, sendName.get());
    }

    @Test
    void builder() {
        Analytics.Builder builder= Analytics.builder(TEST_STRING, TEST_STRING);
        assertNotNull(builder);
    }
}