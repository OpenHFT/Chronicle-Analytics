package net.openhft.chronicle.analytics.internal;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

final class HttpUtilTest {

    private static final String TEST_RESPONSE = "I am here!\n" +
            "And I am also here\n";

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
            assertEquals(singletonList(TEST_RESPONSE.replaceAll("\\s+", " ").trim()), debugResponses);
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

    // These test below are here to make sure that the Sender always completes
    // regardless if there are server errors
    @Test
    void malformedURL() {
        final HttpUtil.Sender sender = new HttpUtil.Sender("euhgu23723fvx27ef327f_very_unlikely_to_ever_exist", "{}", errorResponses::add, debugResponses::add);
        sender.run();
        assertEquals(1, errorResponses.size());
        assertTrue(errorResponses.get(0).contains("MalformedURLException"));
    }

    @Test
    void unknownHost() {
        // the address must have a dot . at the end or it can be assumed to be an unqualified domain name.
        final HttpUtil.Sender sender = new HttpUtil.Sender("http://euhgu23723fvx27ef327f.very.unlikely.to.ever.exist.", "{}", errorResponses::add, debugResponses::add);
        sender.run();
        assertEquals(1, errorResponses.size());
        String msg = errorResponses.get(0);
        assertTrue(msg.contains("UnknownHostException"));
    }

    @Test
    void hungHttpServer() throws IOException {
        final MockWebServer server = new MockWebServer();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final int delayMs = 30_000;
        final int latchPollMs = 100;

        server.setDispatcher(new Dispatcher() {
            @Override
            public @NotNull MockResponse dispatch(@NotNull RecordedRequest recordedRequest) {
                int cnt = 0;
                for (int i = 0; i < delayMs/latchPollMs; i++) {
                    try {
                        if (countDownLatch.await(latchPollMs, TimeUnit.MILLISECONDS))
                            break;
                    } catch (InterruptedException ignore) {
                    }
                }
                return new MockResponse().setBody(TEST_RESPONSE);
            }
        });

        server.start();
        try {
            final HttpUrl url = server.url("mp/collect");
            final HttpUtil.Sender sender = new HttpUtil.Sender(url.url().toString(), "{}", errorResponses::add, debugResponses::add);

            sender.run();
            countDownLatch.countDown();

            assertEquals(1, errorResponses.size());
            assertTrue(errorResponses.get(0).contains("SocketTimeoutException"));
        } finally {
            server.shutdown();
        }
    }
}