package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonContainsKeywordsPredicate;
import seedu.address.testutil.AddressBookBuilder;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests the context overview's counts, ordering and interaction with the person filter.
 */
public class ContextsCommandTest {

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ContextsCommand().execute(null));
    }

    @Test
    public void execute_emptyAddressBook_showsNoContexts() {
        assertCommandSuccess(new ContextsCommand(), new ModelManager(),
                ContextsCommand.MESSAGE_NO_CONTEXTS, new ModelManager());
    }

    @Test
    public void execute_singleContext_showsSingularCountAndSummary() {
        Model model = createModel(createPerson("Alex Tan", "CS2103T"));

        assertCommandSuccess(new ContextsCommand(), model, expectedOverview(contextLine("CS2103T", 1)),
                new ModelManager(model.getAddressBook(), new UserPrefs()));
    }

    @Test
    public void execute_sharedContext_countsEachPerson() {
        Model model = createModel(createPerson("Alex Tan", "CS2103T"), createPerson("Priya Nair", "cs2103t"));

        assertCommandSuccess(new ContextsCommand(), model, expectedOverview(contextLine("CS2103T", 2)),
                new ModelManager(model.getAddressBook(), new UserPrefs()));
    }

    @Test
    public void execute_multipleContexts_groupsNormalisedNamesAndSortsIgnoringCase() {
        Person alex = createPerson("Alex Tan", "nus   Hackers", "cs2103t", "CS2103T", "CS2103", "Zeta");
        Person priya = createPerson("Priya Nair", "NUS Hackers", "CS2103T", "Alpha");
        Person marcus = createPerson("Marcus Lim", "CS2103T");
        Model model = createModel(alex, priya, marcus);
        String expectedMessage = expectedOverview(contextLine("Alpha", 1), contextLine("CS2103", 1),
                contextLine("cs2103t", 3), contextLine("nus Hackers", 2), contextLine("Zeta", 1));

        assertCommandSuccess(new ContextsCommand(), model, expectedMessage,
                new ModelManager(model.getAddressBook(), new UserPrefs()));
    }

    @Test
    public void execute_sameNamePersons_countsSeparateContacts() {
        Model model = createModel(createPerson("Alex Tan", "CS2103T"),
                createPerson("Alex Tan", "CS2103T", "NUS Hackers"));

        assertCommandSuccess(new ContextsCommand(), model,
                expectedOverview(contextLine("CS2103T", 2), contextLine("NUS Hackers", 1)),
                new ModelManager(model.getAddressBook(), new UserPrefs()));
    }

    @Test
    public void execute_filteredList_countsAllContactsAndPreservesFilter() {
        Person alex = createPerson("Alex Tan", "CS2103T");
        Person priya = createPerson("Priya Nair", "NUS Hackers");
        Model model = createModel(alex, priya);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        PersonContainsKeywordsPredicate predicate = new PersonContainsKeywordsPredicate(List.of("Alex"));
        model.updateFilteredPersonList(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(new ContextsCommand(), model,
                expectedOverview(contextLine("CS2103T", 1), contextLine("NUS Hackers", 1)), expectedModel);

        // The existing predicate must still apply to subsequent updates, not just retain a snapshot.
        Person renamedAlex = new PersonBuilder(alex).withName("Marcus Lim").build();
        model.setPerson(alex, renamedAlex);
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void execute_emptyFilteredList_stillListsAllContexts() {
        Model model = createModel(createPerson("Alex Tan", "CS2103T"));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        model.updateFilteredPersonList(person -> false);
        expectedModel.updateFilteredPersonList(person -> false);

        assertCommandSuccess(new ContextsCommand(), model, expectedOverview(contextLine("CS2103T", 1)), expectedModel);
    }

    @Test
    public void execute_addressBookChanges_recomputesCountsAndDisplaySpelling() {
        Person alex = createPerson("Alex Tan", "cs2103t");
        Person priya = createPerson("Priya Nair", "CS2103T");
        Model model = createModel(alex);
        ContextsCommand command = new ContextsCommand();
        assertEquals(expectedOverview(contextLine("cs2103t", 1)), command.execute(model).getFeedbackToUser());

        model.addPerson(priya);
        assertEquals(expectedOverview(contextLine("cs2103t", 2)), command.execute(model).getFeedbackToUser());

        Person editedPriya = new PersonBuilder(priya).withContexts("NUS Hackers").build();
        model.setPerson(priya, editedPriya);
        assertEquals(expectedOverview(contextLine("cs2103t", 1), contextLine("NUS Hackers", 1)),
                command.execute(model).getFeedbackToUser());

        model.setPerson(editedPriya, priya);
        model.deletePerson(alex);
        assertEquals(expectedOverview(contextLine("CS2103T", 1)), command.execute(model).getFeedbackToUser());

        model.setAddressBook(new AddressBook());
        assertEquals(ContextsCommand.MESSAGE_NO_CONTEXTS, command.execute(model).getFeedbackToUser());
    }

    private String contextLine(String context, int count) {
        String messageFormat = count == 1
                ? ContextsCommand.MESSAGE_CONTEXT_SINGULAR : ContextsCommand.MESSAGE_CONTEXT_PLURAL;
        return String.format(messageFormat, context, count);
    }

    private String expectedOverview(String... contextLines) {
        String summaryFormat = contextLines.length == 1
                ? ContextsCommand.MESSAGE_SUCCESS_SINGULAR : ContextsCommand.MESSAGE_SUCCESS_PLURAL;
        return String.join("\n", contextLines) + "\n\n" + String.format(summaryFormat, contextLines.length);
    }

    private Person createPerson(String name, String... contexts) {
        return new PersonBuilder().withName(name).withContexts(contexts).withoutPhone().withoutEmail().build();
    }

    private Model createModel(Person... persons) {
        AddressBookBuilder builder = new AddressBookBuilder();
        for (Person person : persons) {
            builder.withPerson(person);
        }
        return new ModelManager(builder.build(), new UserPrefs());
    }
}
