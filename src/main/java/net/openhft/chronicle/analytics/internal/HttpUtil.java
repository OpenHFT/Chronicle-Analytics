/*
 * Copyright 2016-2020 chronicle.software
 *
 * https://chronicle.software
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

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

final class HttpUtil {

    private static final int DEFAULT_TIME_OUT_MS = 2_000;

    private static final String THREAD_NAME = "chronicle~analytics~http~client";
    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor(runnable -> {
        final Thread thread = new Thread(runnable, THREAD_NAME);
        thread.setDaemon(true);
        return thread;
    });

    private HttpUtil() {
    }

    public static void send(@NotNull final String urlString,
                            @NotNull final String body,
                            @NotNull final Consumer<String> errorLogger,
                            @NotNull final Consumer<String> debugLogger) {
        EXECUTOR.execute(new Sender(urlString, body, errorLogger, debugLogger));
    }

    static String urlEncode(@NotNull final String s, @NotNull final Consumer<String> errorLogger) {
        try {
            return URLEncoder.encode(s, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            errorLogger.accept(e.toString());
            throw new InternalAnalyticsException("This should never happen as " + StandardCharsets.UTF_8.toString() + " should always be present.");
        }
    }

    static final class Sender implements Runnable {

        private final String urlString;
        private final String body;
        private final Consumer<String> errorLogger;
        private final Consumer<String> debugLogger;

        Sender(@NotNull final String urlString,
               @NotNull final String body,
               @NotNull final Consumer<String> errorLogger,
               @NotNull final Consumer<String> debugLogger) {
            this.urlString = urlString;
            this.body = body;
            this.errorLogger = errorLogger;
            this.debugLogger = debugLogger;
        }

        @Override
        public void run() {
            try {
                final URL url = new URL(urlString);
                final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // Do not linger if the connection is slow. Give up instead!
                conn.setConnectTimeout(DEFAULT_TIME_OUT_MS);
                conn.setReadTimeout(DEFAULT_TIME_OUT_MS);
                conn.setRequestMethod("POST");
                //conn.setRequestProperty("Content-Type", "application/json; utf-8"); // For some reason, this does not work...
                conn.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                try (OutputStream os = conn.getOutputStream()) {
                    final byte[] output = body.getBytes(StandardCharsets.UTF_8);
                    os.write(output, 0, output.length);
                    os.flush();
                }

                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    final StringBuilder response = new StringBuilder();
                    String sep = "";
                    for (String responseLine; (responseLine = br.readLine()) != null; ) {
                        response.append(sep).append(responseLine); // preserve some white space
                        sep = " ";
                    }
                    final String logMsg = response.toString().replaceAll("\\s+(?=\\S)", " ");
                    if (!logMsg.isEmpty())
                        debugLogger.accept(logMsg);
                }

            } catch (IOException ioe) {
                errorLogger.accept(ioe.toString());
            }
        }
    }

}