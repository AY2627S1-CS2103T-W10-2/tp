package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.FileUtil;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.ReadOnlyAddressBook;

/**
 * A class to access AddressBook data stored as a JSON file on the hard disk.
 */
public class JsonAddressBookStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonAddressBookStorage.class);

    private Path filePath;

    /** A data file that could not be loaded and may still be in place. It must be backed up before a save. */
    private Path unreadableFile;

    public JsonAddressBookStorage(Path filePath) {
        this.filePath = filePath;
    }

    public Path getAddressBookFilePath() {
        return filePath;
    }

    /**
     * Returns AddressBook data as a {@link ReadOnlyAddressBook}.
     * Returns {@code Optional.empty()} if storage file is not found.
     *
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException {
        return readAddressBook(filePath);
    }

    /**
     * Similar to {@link #readAddressBook()}.
     *
     * @param filePath location of the data. Cannot be null.
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyAddressBook> readAddressBook(Path filePath) throws DataLoadingException {
        requireNonNull(filePath);

        try {
            Optional<JsonSerializableAddressBook> jsonAddressBook = JsonUtil.readJsonFile(
                    filePath, JsonSerializableAddressBook.class);
            if (!jsonAddressBook.isPresent()) {
                return Optional.empty();
            }
            return Optional.of(jsonAddressBook.get().toModelType());
        } catch (IllegalValueException ive) {
            logger.info("Illegal values found in " + filePath + ": " + ive.getMessage());
            unreadableFile = filePath;
            throw new DataLoadingException(ive);
        } catch (DataLoadingException e) {
            unreadableFile = filePath;
            throw e;
        }
    }

    /**
     * Saves the given {@link ReadOnlyAddressBook} to the storage.
     * @param addressBook cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
        saveAddressBook(addressBook, filePath);
    }

    /**
     * Similar to {@link #saveAddressBook(ReadOnlyAddressBook)}.
     *
     * @param filePath location of the data. Cannot be null.
     */
    public void saveAddressBook(ReadOnlyAddressBook addressBook, Path filePath) throws IOException {
        requireNonNull(addressBook);
        requireNonNull(filePath);

        backUpUnreadableFileIfStillThere(filePath);
        FileUtil.createIfMissing(filePath);
        JsonUtil.saveJsonFile(new JsonSerializableAddressBook(addressBook), filePath);
    }

    /**
     * If {@code filePath} is a data file that could not be loaded and is still in place, for example because its
     * folder was not writable when the app started, renames it to a backup before it is overwritten.
     *
     * @throws IOException if the file cannot be backed up. The file is then left as it is and the save is refused.
     */
    private void backUpUnreadableFileIfStillThere(Path filePath) throws IOException {
        if (!filePath.equals(unreadableFile)) {
            return;
        }
        Optional<Path> backup = FileUtil.backUp(filePath);
        backup.ifPresent(path -> logger.warning("The unreadable data file was renamed to " + path));
        // backUp also returns empty when the file's folder cannot be opened, and then the file is still there
        if (backup.isPresent() || Files.notExists(filePath)) {
            unreadableFile = null;
        }
    }

}
