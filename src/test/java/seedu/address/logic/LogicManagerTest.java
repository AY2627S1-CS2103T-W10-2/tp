package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.CommandTestUtil.CONTEXT_DESC_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.AMY;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.ContextsCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;

    @BeforeEach
    public void setUp() {
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        String expectedMessage = String.format(ListCommand.MESSAGE_SUCCESS_PLURAL,
                model.getFilteredPersonList().size());
        assertCommandSuccess(listCommand, expectedMessage, model);
    }

    @Test
    public void execute_contextsAfterFind_preservesDisplayedListAndSurvivesReload() throws Exception {
        logic.execute("add n/Alex Tan c/CS2103T c/NUS Hackers");
        logic.execute("add n/Priya Nair c/cs2103t");
        logic.execute("find Priya");
        List<Person> displayedPersons = List.copyOf(model.getFilteredPersonList());
        String expectedMessage = String.format(ContextsCommand.MESSAGE_CONTEXT_PLURAL, "CS2103T", 2) + "\n"
                + String.format(ContextsCommand.MESSAGE_CONTEXT_SINGULAR, "NUS Hackers", 1) + "\n\n"
                + String.format(ContextsCommand.MESSAGE_SUCCESS_PLURAL, 2);

        assertEquals(new CommandResult(expectedMessage), logic.execute("CoNtExTs ignored"));
        assertEquals(displayedPersons, model.getFilteredPersonList());

        JsonAddressBookStorage storage = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        Model reloadedModel = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        assertEquals(model.getAddressBook(), reloadedModel.getAddressBook());
        assertEquals(new CommandResult(expectedMessage), new ContextsCommand().execute(reloadedModel));
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT,
                temporaryFolder.resolve("ExceptionUserPrefs.json")));
    }

    @Test
    public void execute_storageThrowsIoExceptionOnCommandThatChangesNothing_succeeds() throws Exception {
        logic = new LogicManager(model, createStorageThatThrowsOnSave(DUMMY_IO_EXCEPTION));

        assertEquals(new CommandResult(FindCommand.MESSAGE_NO_PERSONS_FOUND), logic.execute("find nobody"));
    }

    @Test
    public void execute_storageThrowsFileAlreadyExistsExceptionForFile_throwsCommandException() throws IOException {
        Path file = Files.createFile(temporaryFolder.resolve("data"));
        assertCommandFailureForExceptionFromStorage(new FileAlreadyExistsException(file.toString()), String.format(
                LogicManager.FILE_OPS_NOT_A_FOLDER_FORMAT,
                temporaryFolder.resolve("ExceptionUserPrefs.json"), file));
    }

    @Test
    public void execute_storageThrowsFileAlreadyExistsExceptionForOtherPath_throwsCommandException() {
        IOException notAFile = new FileAlreadyExistsException("missing");
        assertCommandFailureForExceptionFromStorage(notAFile, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, notAFile.getMessage()));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredPersonList().remove(0));
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        logic = new LogicManager(model, createStorageThatThrowsOnSave(e));

        // Triggers the saveAddressBook method by executing an add command
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + CONTEXT_DESC_FRIEND;
        // the change is kept for the current session, as the use cases in the developer guide say
        Person expectedPerson = new PersonBuilder(AMY).build();
        ModelManager expectedModel = new ModelManager();
        expectedModel.addPerson(expectedPerson);
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }

    /**
     * Returns a {@code StorageManager} whose address book storage throws {@code e} whenever it is asked to save.
     */
    private StorageManager createStorageThatThrowsOnSave(IOException e) {
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(prefPath) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw e;
            }
        };
        return new StorageManager(addressBookStorage, new JsonUserPrefsStorage(prefPath));
    }
}
