package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class MainAppTest {

    @TempDir
    public Path testFolder;

    @Test
    public void chooseDataFile_onlyOldFileExists_copiesItAndKeepsOldFile() throws IOException {
        Path oldFile = testFolder.resolve("addressbook.json");
        Path newFile = testFolder.resolve("unicontacts.json");
        Files.writeString(oldFile, "{ \"persons\" : [ ] }");

        assertEquals(newFile, MainApp.chooseDataFile(newFile, oldFile));
        assertEquals(Files.readString(oldFile), Files.readString(newFile));
    }

    @Test
    public void chooseDataFile_bothFilesExist_usesNewFileUnchanged() throws IOException {
        Path oldFile = testFolder.resolve("addressbook.json");
        Path newFile = testFolder.resolve("unicontacts.json");
        Files.writeString(oldFile, "old");
        Files.writeString(newFile, "new");

        assertEquals(newFile, MainApp.chooseDataFile(newFile, oldFile));
        assertEquals("new", Files.readString(newFile));
    }

    @Test
    public void chooseDataFile_noOldFile_usesNewFileWithoutCreatingIt() {
        Path oldFile = testFolder.resolve("addressbook.json");
        Path newFile = testFolder.resolve("unicontacts.json");

        assertEquals(newFile, MainApp.chooseDataFile(newFile, oldFile));
        assertFalse(Files.exists(newFile));
    }

    @Test
    public void chooseDataFile_copyFails_usesOldFileAndLeavesNoPartialNewFile() throws IOException {
        Path oldFile = testFolder.resolve("addressbook.json");
        // the new file's folder is a regular file, so the copy cannot be written
        Path blocker = testFolder.resolve("blocker");
        Files.writeString(blocker, "not a folder");
        Path newFile = blocker.resolve("unicontacts.json");
        Files.writeString(oldFile, "old");

        assertEquals(oldFile, MainApp.chooseDataFile(newFile, oldFile));
        assertFalse(Files.exists(newFile));
    }
}
