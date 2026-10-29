package seedu.address.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_FORMAT = "Could not save data due to the following error: %s";

    public static final String FILE_OPS_PERMISSION_ERROR_FORMAT =
            "Could not save data to file %s due to insufficient permissions to write to the file or the folder.";

    public static final String FILE_OPS_NOT_A_FOLDER_FORMAT =
            "Could not save data to file %s because %s exists but is not a folder.";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final AddressBookParser addressBookParser;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
        addressBookParser = new AddressBookParser();
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        CommandResult commandResult;
        Command command = addressBookParser.parseCommand(commandText);
        List<String> savedFormBeforeCommand = describePersons(model.getAddressBook());
        commandResult = command.execute(model);

        // commands that change nothing, such as list and find, do not need the data file to be writable
        if (describePersons(model.getAddressBook()).equals(savedFormBeforeCommand)) {
            return commandResult;
        }

        try {
            storage.saveAddressBook(model.getAddressBook());
        } catch (AccessDeniedException e) {
            throw new CommandException(String.format(FILE_OPS_PERMISSION_ERROR_FORMAT,
                    storage.getAddressBookFilePath()), e);
        } catch (FileAlreadyExistsException | NotDirectoryException e) {
            // FileAlreadyExistsException is also thrown for other causes, such as a link to a missing file
            if (e.getFile() != null && Files.isRegularFile(Path.of(e.getFile()))) {
                throw new CommandException(String.format(FILE_OPS_NOT_A_FOLDER_FORMAT,
                        storage.getAddressBookFilePath(), e.getFile()), e);
            }
            throw new CommandException(String.format(FILE_OPS_ERROR_FORMAT, e.getMessage()), e);
        } catch (IOException ioe) {
            throw new CommandException(String.format(FILE_OPS_ERROR_FORMAT, ioe.getMessage()), ioe);
        }

        return commandResult;
    }

    /**
     * Returns every person, archived or not, as the text that is saved. Persons are not compared with
     * {@code equals} because it ignores capitalisation in contexts and emails, so a change that only
     * affects capitalisation would not be saved.
     */
    private static List<String> describePersons(ReadOnlyAddressBook addressBook) {
        return addressBook.getPersonList().stream().map(Person::toString).collect(Collectors.toList());
    }

    @Override
    public ObservableBooleanValue showingArchivesProperty() {
        return model.showingArchivesProperty();
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return model.getFilteredPersonList();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }
}
