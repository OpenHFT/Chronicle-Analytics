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

package net.openhft.chronicle.analytics.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ClientIdUtilTest {

    @TempDir
    java.nio.file.Path tempDir;

    private java.nio.file.Path clientIdPath;
    private List<String> debugMessages;

    @BeforeEach
    void beforeEach() {
        clientIdPath = tempDir.resolve("client.id");
        debugMessages = new ArrayList<>();
    }

    @Test
    void acquireClientId() {
        // First time
        final String clientId = FilesUtil.acquireClientId(clientIdPath.toString(), debugMessages::add);
        assertDoesNotThrow(() -> UUID.fromString(clientId));
        assertEquals(1, debugMessages.size());
        final String msg = debugMessages.get(0);
        assertTrue(msg.contains("file not present"));
        assertTrue(msg.contains(clientIdPath.toString()));

        // Second time should give the same id
        final List<String> debugMessages2 = new ArrayList<>();
        final String clientId2 = FilesUtil.acquireClientId(clientIdPath.toString(), debugMessages2::add);
        assertEquals(clientId, clientId2);
        assertTrue(debugMessages2.isEmpty());
    }

    @Test
    void acquireClientIdIllegalFile() {
        final String illegalFileName = tempDir.toString();
        final String clientId = FilesUtil.acquireClientId(illegalFileName, debugMessages::add);
        assertNotNull(clientId);

        assertEquals(2, debugMessages.size());
        assertTrue(debugMessages.get(0).contains("file not present"));
        assertTrue(debugMessages.get(1).contains("Unable to create"));

    }
}
