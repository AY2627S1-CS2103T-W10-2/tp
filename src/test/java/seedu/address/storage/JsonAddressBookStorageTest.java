package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.HOON;
import static seedu.address.testutil.TypicalPersons.IDA;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.FileUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Context;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class JsonAddressBookStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonAddressBookStorageTest");

    @TempDir
    public Path testFolder;

    @Test
    public void readAddressBook_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readAddressBook(null));
    }

    // Regression tests: a null inside the data file used to escape as a NullPointerException, which skipped the
    // DataLoadingException recovery in MainApp (data file backed up, app starts empty) and crashed the app.

    @Test
    public void readAddressBook_nullContextEntry_throwsDataLoadingExceptionCausedByIllegalValue() {
        DataLoadingException e = Assertions.assertThrows(DataLoadingException.class, () ->
                readAddressBook("nullContextEntryAddressBook.json"));
        assertTrue(e.getCause() instanceof IllegalValueException);
        assertEquals(String.format(JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT, Context.class.getSimpleName()),
                e.getCause().getMessage());
    }

    @Test
    public void readAddressBook_nullPersonEntry_throwsDataLoadingExceptionCausedByIllegalValue() {
        DataLoadingException e = Assertions.assertThrows(DataLoadingException.class, () ->
                readAddressBook("nullPersonEntryAddressBook.json"));
        assertTrue(e.getCause() instanceof IllegalValueException);
        assertEquals(JsonSerializableAddressBook.MESSAGE_MISSING_PERSON, e.getCause().getMessage());
    }

    @Test
    public void readAddressBook_fileContainingOnlyNull_throwsDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("nullAddressBook.json"));
    }

    @Test
    public void unreadableFile_backedUpThenSaved_originalKeptInBackup() throws Exception {
        Path dataFile = testFolder.resolve("addressbook.json");
        Files.copy(TEST_DATA_FOLDER.resolve("invalidPersonAddressBook.json"), dataFile);
        String originalContent = FileUtil.readFromFile(dataFile);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(dataFile);

        // the file cannot be loaded, so the app would start empty; it renames the file to a backup first
        assertThrows(DataLoadingException.class, storage::readAddressBook);
        Path backup = FileUtil.backUp(dataFile).get();
        assertFalse(Files.exists(dataFile));

        // the next save creates a new data file, but the backup still has the original text
        storage.saveAddressBook(getTypicalAddressBook());
        assertEquals(getTypicalAddressBook(), new AddressBook(storage.readAddressBook().get()));
        assertEquals(originalContent, FileUtil.readFromFile(backup));
    }

    @Test
    public void unreadableFile_notBackedUpBeforeSave_savingBacksItUpFirst() throws Exception {
        Path dataFile = testFolder.resolve("addressbook.json");
        Files.copy(TEST_DATA_FOLDER.resolve("invalidPersonAddressBook.json"), dataFile);
        String originalContent = FileUtil.readFromFile(dataFile);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(dataFile);

        // the app could not rename the file at start-up, so it is still in place when the first save comes
        assertThrows(DataLoadingException.class, storage::readAddressBook);
        storage.saveAddressBook(getTypicalAddressBook());

        assertEquals(getTypicalAddressBook(), new AddressBook(storage.readAddressBook().get()));
        try (java.util.stream.Stream<Path> files = Files.list(testFolder)) {
            Path backup = files.filter(file -> file.getFileName().toString().endsWith(".bak")).findFirst().get();
            assertEquals(originalContent, FileUtil.readFromFile(backup));
        }
    }

    @Test
    public void readAddressBook_folderCannotBeSearched_throwsDataLoadingException() throws Exception {
        org.junit.jupiter.api.Assumptions.assumeTrue(
                java.nio.file.FileSystems.getDefault().supportedFileAttributeViews().contains("posix"));
        Path folder = testFolder.resolve("data");
        Files.createDirectory(folder);
        Path dataFile = folder.resolve("addressbook.json");
        new JsonAddressBookStorage(dataFile).saveAddressBook(getTypicalAddressBook());
        Files.setPosixFilePermissions(folder, java.nio.file.attribute.PosixFilePermissions.fromString("---------"));
        try {
            org.junit.jupiter.api.Assumptions.assumeFalse(Files.isReadable(folder)); // a user that ignores permissions
            // the file is there but cannot be reached: that is not the same as having no data file yet
            assertThrows(DataLoadingException.class, () -> new JsonAddressBookStorage(dataFile).readAddressBook());
        } finally {
            Files.setPosixFilePermissions(folder, java.nio.file.attribute.PosixFilePermissions.fromString("rwx------"));
        }
    }

    private java.util.Optional<ReadOnlyAddressBook> readAddressBook(String filePath) throws Exception {
        return new JsonAddressBookStorage(Paths.get(filePath)).readAddressBook(addToTestDataPathIfNotNull(filePath));
    }

    private Path addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER.resolve(prefsFileInTestDataFolder)
                : null;
    }

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readAddressBook("NonExistentFile.json").isPresent());
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("notJsonFormatAddressBook.json"));
    }

    @Test
    public void readAddressBook_invalidPersonAddressBook_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("invalidPersonAddressBook.json"));
    }

    @Test
    public void readAddressBook_invalidAndValidPersonAddressBook_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("invalidAndValidPersonAddressBook.json"));
    }

    @Test
    public void readAndSaveAddressBook_allInOrder_success() throws Exception {
        Path filePath = testFolder.resolve("TempAddressBook.json");
        AddressBook original = getTypicalAddressBook();
        JsonAddressBookStorage jsonAddressBookStorage = new JsonAddressBookStorage(filePath);

        // Save in new file and read back
        jsonAddressBookStorage.saveAddressBook(original, filePath);
        ReadOnlyAddressBook readBack = jsonAddressBookStorage.readAddressBook(filePath).get();
        assertEquals(original, new AddressBook(readBack));

        // Modify data, overwrite existing file, and read back
        original.addPerson(HOON);
        original.removePerson(ALICE);
        jsonAddressBookStorage.saveAddressBook(original, filePath);
        readBack = jsonAddressBookStorage.readAddressBook(filePath).get();
        assertEquals(original, new AddressBook(readBack));

        // Save and read without specifying file path
        original.addPerson(IDA);
        jsonAddressBookStorage.saveAddressBook(original); // file path not specified
        readBack = jsonAddressBookStorage.readAddressBook().get(); // file path not specified
        assertEquals(original, new AddressBook(readBack));

    }

    @Test
    public void readAndSaveAddressBook_archivedAdditionalDetailsPreserved() throws Exception {
        Path filePath = testFolder.resolve("ContactDetailsAddressBook.json");
        Person person = new PersonBuilder().withAlias("May").withIntroducer("Ben Lee")
                .withNote("Met at Hack&Roll").withArchived(true).build();
        AddressBook original = new AddressBook();
        original.addPerson(person);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);

        storage.saveAddressBook(original);

        assertEquals(original, new AddressBook(storage.readAddressBook().get()));
    }

    @Test
    public void saveAddressBook_nullAddressBook_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveAddressBook(null, "SomeFile.json"));
    }

    /**
     * Saves {@code addressBook} at the specified {@code filePath}.
     */
    private void saveAddressBook(ReadOnlyAddressBook addressBook, String filePath) {
        try {
            new JsonAddressBookStorage(Paths.get(filePath))
                    .saveAddressBook(addressBook, addToTestDataPathIfNotNull(filePath));
        } catch (IOException ioe) {
            throw new AssertionError("There should not be an error writing to the file.", ioe);
        }
    }

    @Test
    public void saveAddressBook_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveAddressBook(new AddressBook(), null));
    }
}
