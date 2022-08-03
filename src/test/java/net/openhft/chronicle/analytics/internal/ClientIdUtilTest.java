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

package net.openhft.chronicle.analytics.internal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ClientIdUtilTest {

    private static final String FILE_NAME = "client.id";
    private List<String> debugMessages;

    @BeforeEach
    void beforeEach() {
        cleanupFile();
        debugMessages = new ArrayList<>();
    }

    @AfterEach
    void afterEach() {
        cleanupFile();
    }

    @Test
    void acquireClientId() {
        // First time
        final String clientId = FilesUtil.acquireClientId(FILE_NAME, debugMessages::add);
        assertDoesNotThrow(() -> UUID.fromString(clientId));
        assertEquals(1, debugMessages.size());
        final String msg = debugMessages.get(0);
        assertTrue(msg.contains("file not present"));
        assertTrue(msg.contains(FILE_NAME));

        // Second time should give the same id
        final List<String> debugMessages2 = new ArrayList<>();
        final String clientId2 = FilesUtil.acquireClientId(FILE_NAME, debugMessages2::add);
        assertEquals(clientId, clientId2);
        assertTrue(debugMessages2.isEmpty());
    }

    @Test
    void acquireClientIdIllegalFile() {
        final String illegalFileName = ".";
        final String clientId = FilesUtil.acquireClientId(illegalFileName, debugMessages::add);
        assertNotNull(clientId);

        assertEquals(2, debugMessages.size());
        assertTrue(debugMessages.get(0).contains("file not present"));
        assertTrue(debugMessages.get(1).contains("Unable to create"));

    }

    private void cleanupFile() {
        new File(FILE_NAME).delete();
    }
}