package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class FileUtilTest {

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
    public void backUp_missingFile_returnsEmpty() throws IOException {
        assertEquals(Optional.empty(), FileUtil.backUp(testFolder.resolve("missing.json")));
    }

    @Test
    public void backUp_existingFile_copiesToBakFile() throws IOException {
        Path file = testFolder.resolve("data.json");
        FileUtil.writeToFile(file, "contacts");

        Path backup = FileUtil.backUp(file).get();

        assertEquals(testFolder.resolve("data.json.bak"), backup);
        assertEquals("contacts", FileUtil.readFromFile(backup));
        assertTrue(Files.exists(file));
    }

    @Test
    public void backUp_existingBackup_isReplaced() throws IOException {
        Path file = testFolder.resolve("data.json");
        FileUtil.writeToFile(file, "first");
        FileUtil.backUp(file);
        FileUtil.writeToFile(file, "second");

        Path backup = FileUtil.backUp(file).get();

        assertEquals("second", FileUtil.readFromFile(backup));
        assertTrue(Files.exists(backup));
    }
}
