package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Context;
import seedu.address.model.person.Person;
import seedu.address.testutil.ContextAddressBook;

public class UnarchiveByContextCommandTest {

    private static final Context CS2103T = new Context("CS2103T");

    private Model model = new ModelManager(ContextAddressBook.get(), new UserPrefs());
    private int matching;

    @BeforeEach
    public void archiveEveryoneWithContext() throws Exception {
        matching = personsWith(CS2103T).size();
        new ArchiveByContextCommand(CS2103T).execute(model);
    }

    private List<Person> personsWith(Context context) {
        return model.getAddressBook().getPersonList().stream()
                .filter(person -> person.getContexts().contains(context)).toList();
    }

    @Test
    public void execute_matchingContext_unarchivesEveryMatchingPerson() throws Exception {
        CommandResult result = new UnarchiveByContextCommand(CS2103T).execute(model);

        assertEquals(String.format(UnarchiveByContextCommand.MESSAGE_UNARCHIVE_SUCCESS, matching, CS2103T),
                result.getFeedbackToUser());
        assertTrue(personsWith(CS2103T).stream().noneMatch(Person::isArchived));
        assertEquals(model.getAddressBook().getPersonList().size(), model.getFilteredPersonList().size());
    }

    @Test
    public void execute_onlyUnarchivesTheGivenContext() throws Exception {
        Context hiking = new Context(ContextAddressBook.HIKING);
        // a person with both contexts is archived, so the other context's person is not touched
        long archivedWithoutHiking = model.getAddressBook().getPersonList().stream()
                .filter(person -> person.isArchived() && !person.getContexts().contains(hiking)).count();
        assertTrue(archivedWithoutHiking > 0);

        new UnarchiveByContextCommand(hiking).execute(model);

        assertEquals(archivedWithoutHiking, model.getAddressBook().getPersonList().stream()
                .filter(Person::isArchived).count());
    }

    @Test
    public void execute_noArchivedPersonWithContext_throwsCommandException() throws Exception {
        new UnarchiveByContextCommand(CS2103T).execute(model);

        assertCommandFailure(new UnarchiveByContextCommand(CS2103T), model,
                String.format(UnarchiveByContextCommand.MESSAGE_NO_PERSONS_TO_UNARCHIVE, CS2103T));
    }

    @Test
    public void execute_filteredList_stillUsesWholeAddressBook() throws Exception {
        // the archived people are hidden, so the shown list is about different people
        model.updateFilteredPersonList(person -> person.equals(ContextAddressBook.ONLY_CHESS));

        new UnarchiveByContextCommand(CS2103T).execute(model);

        assertFalse(model.getAddressBook().getPersonList().stream()
                .anyMatch(person -> person.getContexts().contains(CS2103T) && person.isArchived()));
    }

    @Test
    public void equals() {
        UnarchiveByContextCommand command = new UnarchiveByContextCommand(CS2103T);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new UnarchiveByContextCommand(new Context("cs2103t"))));
        assertFalse(command.equals(null));
        assertFalse(command.equals(1));
        assertFalse(command.equals(new UnarchiveByContextCommand(new Context(ContextAddressBook.HIKING))));
        assertFalse(command.equals(new ArchiveByContextCommand(CS2103T)));
    }

    @Test
    public void toStringMethod() {
        UnarchiveByContextCommand command = new UnarchiveByContextCommand(CS2103T);
        assertEquals(UnarchiveByContextCommand.class.getCanonicalName() + "{context=CS2103T}", command.toString());
    }
}
