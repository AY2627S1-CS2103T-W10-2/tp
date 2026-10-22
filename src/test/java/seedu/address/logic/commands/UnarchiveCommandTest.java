package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
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

public class UnarchiveCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    /** Archives the first person and shows the archived persons, as the archives command does. */
    private Person archiveFirstPersonAndShowArchives() {
        Person person = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person archived = person.copyWithArchived(true);
        model.setPerson(person, archived);
        model.updateFilteredPersonList(Model.PREDICATE_SHOW_ARCHIVED);
        return archived;
    }

    @Test
    public void execute_inArchivesView_unarchivesThePerson() {
        Person archived = archiveFirstPersonAndShowArchives();
        Person restored = archived.copyWithArchived(false);
        String expectedMessage = String.format(UnarchiveCommand.MESSAGE_UNARCHIVE_PERSON_SUCCESS,
                Messages.format(restored));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(Model.PREDICATE_SHOW_ARCHIVED);
        expectedModel.setPerson(archived, restored);

        assertCommandSuccess(new UnarchiveCommand(INDEX_FIRST_PERSON), model, expectedMessage, expectedModel);

        // the person left the archives and is back in the normal list
        assertTrue(model.getFilteredPersonList().isEmpty());
        model.updateFilteredPersonList(Model.PREDICATE_SHOW_ACTIVE);
        assertTrue(model.getFilteredPersonList().contains(restored));
    }

    @Test
    public void execute_inNormalList_isRejectedAndChangesNothing() {
        // the archives are not shown, so index 1 is an active person and not an archived one
        assertFalse(model.getFilteredPersonList().get(0).isArchived());

        assertCommandFailure(new UnarchiveCommand(INDEX_FIRST_PERSON), model, UnarchiveCommand.MESSAGE_NOT_ARCHIVED);
    }

    @Test
    public void execute_emptyArchives_throwsInvalidIndex() {
        model.updateFilteredPersonList(Model.PREDICATE_SHOW_ARCHIVED);
        assertEquals(0, model.getFilteredPersonList().size());

        assertCommandFailure(new UnarchiveCommand(INDEX_FIRST_PERSON), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_indexOutOfRange_throwsCommandException() {
        archiveFirstPersonAndShowArchives();
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);

        assertCommandFailure(new UnarchiveCommand(outOfBoundIndex), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        UnarchiveCommand unarchiveFirstCommand = new UnarchiveCommand(INDEX_FIRST_PERSON);
        assertTrue(unarchiveFirstCommand.equals(unarchiveFirstCommand));
        assertTrue(unarchiveFirstCommand.equals(new UnarchiveCommand(INDEX_FIRST_PERSON)));
        assertFalse(unarchiveFirstCommand.equals(1));
        assertFalse(unarchiveFirstCommand.equals(null));
        assertFalse(unarchiveFirstCommand.equals(new UnarchiveCommand(INDEX_SECOND_PERSON)));
    }
}
