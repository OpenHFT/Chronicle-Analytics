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
            assertNotNull(System.getProperty("user.home")); // ensure property remains accessible
        } finally {
            System.setProperty("user.home", originalUserHome);
        }
    }
}
