package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;

public class ViewArchivesCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    private void archive(int zeroBasedIndex) {
        Person person = model.getFilteredPersonList().get(zeroBasedIndex);
        model.setPerson(person, person.withArchived(true));
    }

    @Test
    public void execute_noArchivedContacts_showsEmptyMessage() {
        ModelManager expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(Model.PREDICATE_SHOW_ARCHIVED);

        assertCommandSuccess(new ViewArchivesCommand(), model,
                ViewArchivesCommand.MESSAGE_NO_ARCHIVED_CONTACTS, expectedModel);
        assertTrue(model.showingArchivesProperty().get());
    }

    @Test
    public void execute_oneArchivedContact_showsOnlyThatContact() {
        archive(0);
        Person archived = model.getAddressBook().getPersonList().stream().filter(Person::isArchived).findFirst().get();

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(Model.PREDICATE_SHOW_ARCHIVED);

        assertCommandSuccess(new ViewArchivesCommand(), model,
                String.format(ViewArchivesCommand.MESSAGE_SUCCESS_SINGULAR, 1), expectedModel);
        assertEquals(List.of(archived), model.getFilteredPersonList());
    }

    @Test
    public void execute_severalArchivedContacts_showsOnlyArchivedOnes() {
        archive(0);
        archive(0);

        new ViewArchivesCommand().execute(model);

        assertEquals(2, model.getFilteredPersonList().size());
        assertTrue(model.getFilteredPersonList().stream().allMatch(Person::isArchived));
        assertEquals(String.format(ViewArchivesCommand.MESSAGE_SUCCESS_PLURAL, 2),
                new ViewArchivesCommand().execute(model).getFeedbackToUser());
    }

    @Test
    public void execute_afterList_hidesTheArchivesAgain() {
        archive(0);
        new ViewArchivesCommand().execute(model);
        assertTrue(model.showingArchivesProperty().get());

        new ListCommand().execute(model);

        assertFalse(model.showingArchivesProperty().get());
        assertTrue(model.getFilteredPersonList().stream().noneMatch(Person::isArchived));
    }
}
