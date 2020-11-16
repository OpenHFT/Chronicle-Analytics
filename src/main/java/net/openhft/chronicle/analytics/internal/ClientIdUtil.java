package net.openhft.chronicle.analytics.internal;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

enum ClientIdUtil {
    ;

    // This tries to read a client id from a "cookie" file in the
    // user's home directory. If that fails, a new random clientId
    // is generated and an attempt is made to save it in said file.
    static String acquireClientId(@NotNull final String clientIdFileName, @NotNull final Consumer<String> debugLogger) {
        final Path path = Paths.get(clientIdFileName);
        try {
            try (Stream<String> lines = Files.lines(path, UTF_8)) {
                return lines
                        .findFirst()
                        .map(UUID::fromString)
                        .orElseThrow(NoSuchElementException::new)
                        .toString();
            }
        } catch (Exception e) {
            debugLogger.accept("Client id file not present: " + path.toAbsolutePath() + ' ' + e);
        }
        final String id = UUID.randomUUID().toString();
        try {
            Files.write(path, id.getBytes(UTF_8));
        } catch (IOException ioe) {
            debugLogger.accept("Unable to create client id file: " + path.toAbsolutePath() + ' ' + ioe);
        }
        return id;
    }

}