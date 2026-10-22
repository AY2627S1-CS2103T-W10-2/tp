package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;

public class ArchiveCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndex_archivesAndHidesThePerson() {
        Person personToArchive = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        int sizeBefore = model.getFilteredPersonList().size();
        Person archivedPerson = personToArchive.copyWithArchived(true);
        String expectedMessage = String.format(ArchiveCommand.MESSAGE_ARCHIVE_PERSON_SUCCESS,
                Messages.format(archivedPerson));

        ModelManager expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.setPerson(personToArchive, archivedPerson);

        assertCommandSuccess(new ArchiveCommand(INDEX_FIRST_PERSON), model, expectedMessage, expectedModel);

        // the person is still in the address book, but no longer in the normal list
        assertTrue(model.getAddressBook().getPersonList().contains(archivedPerson));
        assertEquals(sizeBefore - 1, model.getFilteredPersonList().size());
        assertFalse(model.getFilteredPersonList().contains(archivedPerson));
    }

    @Test
    public void execute_alreadyArchived_throwsCommandException() {
        Person person = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        model.setPerson(person, person.copyWithArchived(true));
        model.updateFilteredPersonList(Model.PREDICATE_SHOW_ARCHIVED);

        assertCommandFailure(new ArchiveCommand(INDEX_FIRST_PERSON), model, ArchiveCommand.MESSAGE_ALREADY_ARCHIVED);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);

        assertCommandFailure(new ArchiveCommand(outOfBoundIndex), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_filteredList_archivesTheShownPerson() throws Exception {
        // the first displayed person is the second person in the full list
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        Person shownPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        new ArchiveCommand(INDEX_FIRST_PERSON).execute(model);

        assertTrue(model.getAddressBook().getPersonList().contains(shownPerson.copyWithArchived(true)));
        assertFalse(model.getAddressBook().getPersonList().contains(shownPerson));
    }

    @Test
    public void equals() {
        ArchiveCommand archiveFirstCommand = new ArchiveCommand(INDEX_FIRST_PERSON);
        assertTrue(archiveFirstCommand.equals(archiveFirstCommand));
        assertTrue(archiveFirstCommand.equals(new ArchiveCommand(INDEX_FIRST_PERSON)));
        assertFalse(archiveFirstCommand.equals(1));
        assertFalse(archiveFirstCommand.equals(null));
        assertFalse(archiveFirstCommand.equals(new ArchiveCommand(INDEX_SECOND_PERSON)));
    }
}
