package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Context;
import seedu.address.model.person.Person;
import seedu.address.testutil.ContextAddressBook;

public class ArchiveByContextCommandTest {

    private static final Context CS2103T = new Context("CS2103T");

    private Model model = new ModelManager(ContextAddressBook.get(), new UserPrefs());

    private List<Person> personsWith(Context context) {
        return model.getAddressBook().getPersonList().stream()
                .filter(person -> person.getContexts().contains(context)).toList();
    }

    @Test
    public void execute_matchingContext_archivesEveryMatchingPerson() throws Exception {
        int matching = personsWith(CS2103T).size();
        int total = model.getAddressBook().getPersonList().size();
        assertEquals(3, matching);
        assertEquals(5, total);

        CommandResult result = new SetArchivedByContextCommand(CS2103T, true).execute(model);

        assertEquals(String.format(SetArchivedByContextCommand.MESSAGE_ARCHIVE_SUCCESS, matching, CS2103T),
                result.getFeedbackToUser());
        assertTrue(personsWith(CS2103T).stream().allMatch(Person::isArchived));
        assertEquals(total - matching, model.getFilteredPersonList().size());
        assertEquals(total, model.getAddressBook().getPersonList().size());
    }

    @Test
    public void execute_differentCase_archivesMatchingPersons() throws Exception {
        int matching = personsWith(CS2103T).size();

        new SetArchivedByContextCommand(new Context("cs2103t"), true).execute(model);

        assertEquals(matching, model.getAddressBook().getPersonList().stream().filter(Person::isArchived).count());
    }

    @Test
    public void execute_substringOfContext_throwsCommandException() {
        Context partial = new Context("CS");

        assertCommandFailure(new SetArchivedByContextCommand(partial, true), model,
                String.format(SetArchivedByContextCommand.MESSAGE_NO_PERSONS_TO_ARCHIVE, partial));
    }

    @Test
    public void execute_alreadyArchivedPersonsAreSkipped() throws Exception {
        Person first = personsWith(CS2103T).get(0);
        model.setPerson(first, first.copyWithArchived(true));
        int stillActive = personsWith(CS2103T).size() - 1;

        CommandResult result = new SetArchivedByContextCommand(CS2103T, true).execute(model);

        assertEquals(String.format(SetArchivedByContextCommand.MESSAGE_ARCHIVE_SUCCESS, stillActive, CS2103T),
                result.getFeedbackToUser());
    }

    @Test
    public void execute_allAlreadyArchived_throwsCommandException() throws Exception {
        new SetArchivedByContextCommand(CS2103T, true).execute(model);

        assertCommandFailure(new SetArchivedByContextCommand(CS2103T, true), model,
                String.format(SetArchivedByContextCommand.MESSAGE_NO_PERSONS_TO_ARCHIVE, CS2103T));
    }

    @Test
    public void execute_filteredList_stillUsesWholeAddressBook() throws Exception {
        int matching = personsWith(CS2103T).size();
        model.updateFilteredPersonList(person -> person.equals(ContextAddressBook.ONLY_CHESS));
        assertEquals(1, model.getFilteredPersonList().size());

        new SetArchivedByContextCommand(CS2103T, true).execute(model);

        assertEquals(matching, model.getAddressBook().getPersonList().stream().filter(Person::isArchived).count());
        assertFalse(model.getAddressBook().getPersonList().stream()
                .anyMatch(person -> person.getContexts().contains(CS2103T) && !person.isArchived()));
    }

    @Test
    public void equals() {
        SetArchivedByContextCommand command = new SetArchivedByContextCommand(CS2103T, true);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new SetArchivedByContextCommand(new Context("cs2103t"), true)));
        assertFalse(command.equals(null));
        assertFalse(command.equals(1));
        assertFalse(command.equals(new SetArchivedByContextCommand(new Context(ContextAddressBook.HIKING), true)));

        // same context, different action
        assertFalse(command.equals(new SetArchivedByContextCommand(CS2103T, false)));
    }

    @Test
    public void toStringMethod() {
        SetArchivedByContextCommand command = new SetArchivedByContextCommand(CS2103T, true);
        assertEquals(SetArchivedByContextCommand.class.getCanonicalName() + "{context=CS2103T, archived=true}",
                command.toString());
    }
}
