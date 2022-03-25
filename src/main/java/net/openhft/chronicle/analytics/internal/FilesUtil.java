package net.openhft.chronicle.analytics.internal;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

enum FilesUtil {
    ; // none

    private static final String CHRONICLE_ANALYTICS_LAST_FILE_NAME = "/.chronicle.analytics.last";

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

    static boolean isSameAsLastUsedFileTimeStampSecond(final int currentSecondOfDay) {
        final Path path = lastPath();
        try (Stream<String> lines = Files.lines(path, UTF_8)) {
            final int secondOfDay = lines
                    .findFirst()
                    .map(Integer::parseInt)
                    .orElse(0);
            final boolean same = (secondOfDay == currentSecondOfDay);
            if (!same) {
                // We are on a new second so store the new second
                touchLastContent(path);
            }
            return same;
        } catch (IOException fileNotFound) {
            touchLastContent(path);
        }
        return false;
    }

    static void touchLastContent(Path path) {
        try {
            Files.write(path, Integer.toString(LocalTime.now().toSecondOfDay()).getBytes(UTF_8));
        } catch (IOException ignored) {
            // Not much we can do about it...
        }
    }

    // Just for test purposes
    static void removeLastUsedFileTimeStampSecond() {
        try {
            Files.deleteIfExists(lastPath());
        } catch (IOException ioe) {
            // Just for testing so we do not care so much
            ioe.printStackTrace();
        }
    }

    private static Path lastPath() {
        final String fileName = Optional.ofNullable(System.getProperty("user.home"))
                .orElse(".") +
                CHRONICLE_ANALYTICS_LAST_FILE_NAME;
        return Paths.get(fileName);
    }

}