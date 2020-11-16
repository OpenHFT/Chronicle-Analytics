package net.openhft.chronicle.analytics.internal;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HttpUtilTest {

    // https://github.com/square/okhttp/tree/master/mockwebserver

    @Test
    void send() {

        final MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody("ok"));
        // server.start();



    }
}