package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;

public class ClearCommandTest {

    @Test
    public void execute_emptyAddressBook_success() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_nonEmptyAddressBook_success() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.setAddressBook(new AddressBook());

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    private void archiveFirstPeople(Model model, int count) {
        for (int i = 0; i < count; i++) {
            Person person = model.getFilteredPersonList().get(0);
            model.setPerson(person, person.copyWithArchived(true));
        }
    }

    @Test
    public void execute_oneArchivedPerson_keepsIt() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        archiveFirstPeople(model, 1);
        Person archived = model.getAddressBook().getPersonList().stream().filter(Person::isArchived).findFirst().get();

        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        AddressBook archivedOnly = new AddressBook();
        archivedOnly.addPerson(archived);
        expectedModel.setAddressBook(archivedOnly);

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS_KEEPING_ONE_ARCHIVED,
                expectedModel);
        assertEquals(1, model.getAddressBook().getPersonList().size());
        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    @Test
    public void execute_severalArchivedPersons_keepsThemAll() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        archiveFirstPeople(model, 3);

        String message = new ClearCommand().execute(model).getFeedbackToUser();

        assertEquals(String.format(ClearCommand.MESSAGE_SUCCESS_KEEPING_ARCHIVED, 3), message);
        assertEquals(3, model.getAddressBook().getPersonList().size());
        assertTrue(model.getAddressBook().getPersonList().stream().allMatch(Person::isArchived));
    }

    @Test
    public void execute_fromArchivesView_returnsToTheNormalList() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        archiveFirstPeople(model, 1);
        model.updateFilteredPersonList(Model.PREDICATE_SHOW_ARCHIVED);

        new ClearCommand().execute(model);

        assertFalse(model.showingArchivesProperty().get());
        // the archived person is still there, in the archives
        model.updateFilteredPersonList(Model.PREDICATE_SHOW_ARCHIVED);
        assertEquals(1, model.getFilteredPersonList().size());
    }
}
