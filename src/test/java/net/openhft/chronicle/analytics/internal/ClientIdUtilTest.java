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