/*
 * Copyright 2013-2025 chronicle.software; SPDX-License-Identifier: Apache-2.0
 */
package net.openhft.chronicle.analytics.internal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FilesUtilTest {

    @TempDir
    Path tempDir;

    @Test
    void touchLastContentHandlesMissingParent() {
        final Path missingParentFile = tempDir.resolve("missing").resolve("chronicle-last.txt");
        assertDoesNotThrow(() -> FilesUtil.touchLastContent(missingParentFile));
    }

    @Test
    void removeLastUsedFileTimeStampSecondHandlesDirectory() throws IOException {
        final Path customHome = tempDir.resolve("home");
        Files.createDirectories(customHome);
        final String originalUserHome = System.getProperty("user.home");
        try {
            System.setProperty("user.home", customHome.toString());
            final Path directory = customHome.resolve(".chronicle.analytics.last");
            Files.createDirectories(directory.resolve("child"));
            assertDoesNotThrow(FilesUtil::removeLastUsedFileTimeStampSecond);
            assertNotNull(System.getProperty("user.home"), "user.home remains accessible"); // ensure property remains accessible
        } finally {
            System.setProperty("user.home", originalUserHome);
        }
    }
}
