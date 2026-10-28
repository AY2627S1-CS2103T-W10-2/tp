package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class FileUtilTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-10-15T10:17:23Z"), ZoneOffset.UTC);

    @TempDir
    public Path testFolder;

    @Test
    public void writeToFile_newFile_createsFileWithContent() throws IOException {
        Path file = testFolder.resolve("data.json");
        FileUtil.writeToFile(file, "hello");
        assertEquals("hello", FileUtil.readFromFile(file));
    }

    @Test
    public void writeToFile_existingFile_replacesContent() throws IOException {
        Path file = testFolder.resolve("data.json");
        FileUtil.writeToFile(file, "old content that is longer");
        FileUtil.writeToFile(file, "new");
        assertEquals("new", FileUtil.readFromFile(file));
    }

    @Test
    public void writeToFile_leavesNoTemporaryFilesBehind() throws IOException {
        Path file = testFolder.resolve("data.json");
        FileUtil.writeToFile(file, "one");
        FileUtil.writeToFile(file, "two");
        try (Stream<Path> files = Files.list(testFolder)) {
            assertEquals(1, files.count());
        }
    }

    @Test
    public void writeToFile_symbolicLink_writesTargetAndKeepsLink() throws IOException {
        Path target = testFolder.resolve("real.json");
        Path link = testFolder.resolve("link.json");
        FileUtil.writeToFile(target, "one");
        try {
            Files.createSymbolicLink(link, target.getFileName());
        } catch (UnsupportedOperationException | IOException e) {
            assumeTrue(false, "symbolic links are not available");
        }
        FileUtil.writeToFile(link, "two");
        assertTrue(Files.isSymbolicLink(link));
        assertEquals("two", FileUtil.readFromFile(target));
    }

    @Test
    public void backUp_missingFile_returnsEmpty() throws IOException {
        assertEquals(Optional.empty(), FileUtil.backUp(testFolder.resolve("missing.json")));
    }

    @Test
    public void backUp_existingFile_renamesToTimestampedBakFile() throws IOException {
        Path file = testFolder.resolve("data.json");
        FileUtil.writeToFile(file, "contacts");

        Path backup = FileUtil.backUp(file, FIXED_CLOCK).get();

        assertEquals(testFolder.resolve("data.json.20261015T101723Z.bak"), backup);
        assertEquals("contacts", FileUtil.readFromFile(backup));
        assertFalse(Files.exists(file));
    }

    @Test
    public void backUp_publicVersion_namesBackupWithUtcTime() throws IOException {
        Path file = testFolder.resolve("data.json");
        FileUtil.writeToFile(file, "contacts");

        Path backup = FileUtil.backUp(file).get();

        assertTrue(backup.getFileName().toString().matches("data\\.json\\.\\d{8}T\\d{6}Z\\.bak"));
        assertEquals("contacts", FileUtil.readFromFile(backup));
    }

    @Test
    public void backUp_laterBackup_keepsEarlierBackup() throws IOException {
        Path file = testFolder.resolve("data.json");
        FileUtil.writeToFile(file, "first");
        Path first = FileUtil.backUp(file, FIXED_CLOCK).get();
        FileUtil.writeToFile(file, "second");

        Path second = FileUtil.backUp(file, Clock.offset(FIXED_CLOCK, Duration.ofSeconds(5))).get();

        assertEquals("first", FileUtil.readFromFile(first));
        assertEquals("second", FileUtil.readFromFile(second));
    }

    @Test
    public void backUp_sameSecond_replacesBackupWithSameName() throws IOException {
        Path file = testFolder.resolve("data.json");
        FileUtil.writeToFile(file, "first");
        FileUtil.backUp(file, FIXED_CLOCK);
        FileUtil.writeToFile(file, "second");

        Path backup = FileUtil.backUp(file, FIXED_CLOCK).get();

        assertEquals("second", FileUtil.readFromFile(backup));
    }
}
