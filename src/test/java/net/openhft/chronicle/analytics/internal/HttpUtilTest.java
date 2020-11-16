package net.openhft.chronicle.analytics.internal;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

final class HttpUtilTest {

    private static final String TEST_RESPONSE = "I am here!";

    private List<String> debugResponses = new ArrayList<>();
    private List<String> errorResponses = new ArrayList<>();

    @BeforeEach
    void beforeEach() {
        debugResponses = new ArrayList<>();
        errorResponses = new ArrayList<>();
    }

    // https://github.com/square/okhttp/tree/master/mockwebserver

    @Test
    void send() throws IOException {

        final MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(TEST_RESPONSE));

        server.start();
        try {
            final HttpUrl url = server.url("mp/collect");
            final HttpUtil.Sender sender = new HttpUtil.Sender(url.url().toString(), "{}", errorResponses::add, debugResponses::add);

            sender.run();

            assertTrue(errorResponses.isEmpty());
            assertEquals(singletonList(TEST_RESPONSE), debugResponses);
        } finally {
            server.shutdown();
        }

    }

    @Test
    void sendIllegalURL() {
        final HttpUtil.Sender sender = new HttpUtil.Sender("sdjkbhh131921gavsbjaj1j11jg1gvaskaj", "{}", errorResponses::add, debugResponses::add);
        sender.run();
        assertFalse(errorResponses.isEmpty());
        assertTrue(debugResponses.isEmpty());
    }

    @Test
    void urlEncode() {
        final List<String> logMessages = new ArrayList<>();
        final String expected = "A+%25%40%26%5Ea";
        final String actual = HttpUtil.urlEncode("A %@&^a", logMessages::add);
        assertEquals(expected, actual);
        assertTrue(logMessages.isEmpty());
    }

}