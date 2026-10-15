package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.testutil.AddressBookBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ListCommand.
 */
public class ListCommandTest {

    private Model model;
    private Model expectedModel;
    private String expectedMessage;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedMessage = String.format(
                ListCommand.MESSAGE_SUCCESS_PLURAL,
                expectedModel.getFilteredPersonList().size());
    }

    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        assertCommandSuccess(new ListCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_listIsFiltered_showsEverything() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertCommandSuccess(new ListCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_emptyList_showsZeroPeople() {
        Model emptyModel = new ModelManager();
        Model expectedModel = new ModelManager();
        String expectedMessage = String.format(ListCommand.MESSAGE_SUCCESS_PLURAL, 0);

        assertCommandSuccess(new ListCommand(), emptyModel, expectedMessage, expectedModel);
    }

    @Test
    public void execute_singlePerson_showsOnePerson() {
        Model singlePersonModel = new ModelManager(
                new AddressBookBuilder().withPerson(ALICE).build(), new UserPrefs());
        Model expectedSinglePersonModel = new ModelManager(singlePersonModel.getAddressBook(), new UserPrefs());
        String expectedSinglePersonMessage = String.format(ListCommand.MESSAGE_SUCCESS_SINGULAR, 1);

        assertCommandSuccess(new ListCommand(), singlePersonModel,
                expectedSinglePersonMessage, expectedSinglePersonModel);
    }
}
