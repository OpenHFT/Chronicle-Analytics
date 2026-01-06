/*
 * Copyright 2013-2025 chronicle.software; SPDX-License-Identifier: Apache-2.0
 */
package net.openhft.chronicle.analytics.internal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
        assertDoesNotThrow(() -> UUID.fromString(clientId), "clientId is a UUID");
        assertEquals(1, debugMessages.size(), "logs when clientId file is missing");
        final String msg = debugMessages.get(0);
        assertTrue(msg.contains("file not present"), "message indicates missing file");
        assertTrue(msg.contains(FILE_NAME), "message includes file name");

        // Second time should give the same id
        final List<String> debugMessages2 = new ArrayList<>();
        final String clientId2 = FilesUtil.acquireClientId(FILE_NAME, debugMessages2::add);
        assertEquals(clientId, clientId2, "reuses existing clientId");
        assertTrue(debugMessages2.isEmpty(), "no debug output when clientId file exists");
    }

    @Test
    void acquireClientIdIllegalFile() {
        final String illegalFileName = ".";
        final String clientId = FilesUtil.acquireClientId(illegalFileName, debugMessages::add);
        assertNotNull(clientId, "returns fallback clientId");

        assertEquals(2, debugMessages.size(), "logs missing file and create failure");
        assertTrue(debugMessages.get(0).contains("file not present"), "first message indicates missing file");
        assertTrue(debugMessages.get(1).contains("Unable to create"), "second message indicates create failure");

    }

    private void cleanupFile() {
        try {
            Files.deleteIfExists(new File(FILE_NAME).toPath());
        } catch (IOException e) {
            LoggerFactory.getLogger(getClass()).warn("Unable to delete file: " + FILE_NAME, e);
        }
    }
}
