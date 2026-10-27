package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.FindCommand.MESSAGE_NO_PERSONS_FOUND;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.ELLE;
import static seedu.address.testutil.TypicalPersons.FIONA;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonContainsKeywordsPredicate;
import seedu.address.model.person.PersonMatchesCluesPredicate;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code FindCommand}.
 */
public class FindCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals() {
        PersonContainsKeywordsPredicate firstPredicate =
                new PersonContainsKeywordsPredicate(List.of("first"));
        PersonContainsKeywordsPredicate secondPredicate =
                new PersonContainsKeywordsPredicate(List.of("second"));

        FindCommand findFirstCommand = new FindCommand(firstPredicate);
        FindCommand findSecondCommand = new FindCommand(secondPredicate);

        // same object -> returns true
        assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values -> returns true
        FindCommand findFirstCommandCopy = new FindCommand(firstPredicate);
        assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        assertFalse(findFirstCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    @Test
    public void execute_zeroKeywords_noPersonFound() {
        PersonContainsKeywordsPredicate predicate = preparePredicate(" ");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, MESSAGE_NO_PERSONS_FOUND, expectedModel);
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void execute_multipleKeywords_multiplePersonsFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3);
        PersonContainsKeywordsPredicate predicate = preparePredicate("Kurz Elle Kunz");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(CARL, ELLE, FIONA), model.getFilteredPersonList());
    }

    @Test
    public void execute_multipleClues_matchingPersonFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        PersonMatchesCluesPredicate predicate = new PersonMatchesCluesPredicate(
                Optional.of("Alice"), List.of("2103"));
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(ALICE), model.getFilteredPersonList());
    }

    @Test
    public void execute_oneClueDoesNotMatch_noPersonFound() {
        PersonMatchesCluesPredicate predicate = new PersonMatchesCluesPredicate(
                Optional.of("Alice"), List.of("Internship"));
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model, MESSAGE_NO_PERSONS_FOUND, expectedModel);
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void execute_allSupportedClues_matchingPersonFound() {
        Person tony = new PersonBuilder()
                .withName("Tony Zhang")
                .withPhone("91234567")
                .withEmail("tony@example.com")
                .withContexts("CS2103T", "Hack&Roll")
                .withAlias("Toto")
                .withIntroducer("Alice Tan")
                .withNote("Project teammate")
                .build();
        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(tony);
        Model modelWithTony = new ModelManager(addressBook, new UserPrefs());
        Model expectedModelWithTony = new ModelManager(addressBook, new UserPrefs());
        PersonMatchesCluesPredicate predicate = new PersonMatchesCluesPredicate(
                Optional.of("ton"), Optional.of("1234"), Optional.of("@example"),
                List.of("2103", "hack"), Optional.of("tot"), Optional.of("alice"),
                Optional.of("project"));
        expectedModelWithTony.updateFilteredPersonList(Model.PREDICATE_SHOW_ACTIVE.and(predicate));

        assertCommandSuccess(new FindCommand(predicate), modelWithTony,
                String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1), expectedModelWithTony);
        assertEquals(List.of(tony), modelWithTony.getFilteredPersonList());
    }

    @Test
    public void toStringMethod() {
        PersonContainsKeywordsPredicate predicate = new PersonContainsKeywordsPredicate(List.of("keyword"));
        FindCommand findCommand = new FindCommand(predicate);
        String expected = FindCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, findCommand.toString());
    }

    /**
     * Parses {@code userInput} into a {@code PersonContainsKeywordsPredicate}.
     */
    private PersonContainsKeywordsPredicate preparePredicate(String userInput) {
        return new PersonContainsKeywordsPredicate(List.of(userInput.split("\\s+")));
    }

    @Test
    public void execute_archivedPerson_isNeverFound() {
        // Carl matches the keyword, but he is archived
        Person carl = model.getFilteredPersonList().stream()
                .filter(p -> p.equals(CARL)).findFirst().get();
        model.setPerson(carl, carl.copyWithArchived(true));
        FindCommand command = new FindCommand(new PersonContainsKeywordsPredicate(List.of("Kurz")));

        assertEquals(MESSAGE_NO_PERSONS_FOUND, command.execute(model).getFeedbackToUser());
        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    @Test
    public void execute_findFromArchivesView_leavesTheArchives() {
        model.updateFilteredPersonList(Model.PREDICATE_SHOW_ARCHIVED);
        assertTrue(model.showingArchivesProperty().get());

        new FindCommand(new PersonContainsKeywordsPredicate(List.of("Kurz"))).execute(model);

        assertFalse(model.showingArchivesProperty().get());
        assertEquals(List.of(CARL), model.getFilteredPersonList());
    }
}
