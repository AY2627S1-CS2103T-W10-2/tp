package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CONTEXT_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditCommand.
 */
public class EditCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_archivedPerson_isRejectedAndNothingChanges() {
        Person person = model.getFilteredPersonList().get(0);
        model.setPerson(person, person.copyWithArchived(true));
        model.updateFilteredPersonList(Model.PREDICATE_SHOW_ARCHIVED);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build();

        assertCommandFailure(new EditCommand(INDEX_FIRST_PERSON, descriptor), model,
                EditCommand.MESSAGE_ARCHIVED_NOT_EDITABLE);
    }

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Person editedPerson = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastPerson = Index.fromOneBased(model.getFilteredPersonList().size());
        Person lastPerson = model.getFilteredPersonList().get(indexLastPerson.getZeroBased());

        // +c/ keeps the person's existing context
        PersonBuilder personInList = new PersonBuilder(lastPerson);
        Person editedPerson = personInList.withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .withContexts(PersonBuilder.DEFAULT_CONTEXT, VALID_CONTEXT_HUSBAND).build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).withContextsToAdd(VALID_CONTEXT_HUSBAND).build();
        EditCommand editCommand = new EditCommand(indexLastPerson, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(lastPerson, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_noChange() {
        assertNoChange(INDEX_FIRST_PERSON, new EditPersonDescriptor());
    }

    @Test
    public void execute_filteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personInFilteredList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(personInFilteredList).withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personInFilteredList, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_editPhone_keepsAliasIntroducerAndNote() {
        Person alice = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person aliceWithDetails = new PersonBuilder(alice)
                .withAlias("Al").withIntroducer("Ben Lim").withNote("Met at NUS Hackers").build();
        model.setPerson(alice, aliceWithDetails);

        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_BOB).build());

        // only the phone changes; alias, introducer and note are kept
        Person editedPerson = new PersonBuilder(aliceWithDetails).withPhone(VALID_PHONE_BOB).build();
        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(aliceWithDetails, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_removeContext_success() {
        // Benson has the contexts "Hiking club" and "CS2103T"
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withContextsToRemove("Hiking club").build();
        Person editedBenson = new PersonBuilder(BENSON).withContexts("CS2103T").build();

        assertEditSuccess(INDEX_SECOND_PERSON, descriptor, editedBenson);
    }

    @Test
    public void execute_swapOnlyContext_success() {
        // Alice's only context is "CS2103T"; removals happen before additions, so she is never left without one
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withContextsToRemove("CS2103T").withContextsToAdd("NUS Hackers").build();
        Person editedAlice = new PersonBuilder(ALICE).withContexts("NUS Hackers").build();

        assertEditSuccess(INDEX_FIRST_PERSON, descriptor, editedAlice);
    }

    @Test
    public void execute_fixContextCapitalisation_success() {
        // contexts match ignoring case, so only the success message shows the new spelling
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withContextsToRemove("CS2103T").withContextsToAdd("cs2103t").build();
        Person editedAlice = new PersonBuilder(ALICE).withContexts("cs2103t").build();

        assertEditSuccess(INDEX_FIRST_PERSON, descriptor, editedAlice);
    }

    @Test
    public void execute_addExistingContext_noChange() {
        // Alice already has "CS2103T", so "cs2103t" is skipped and her existing spelling is kept
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withContextsToAdd("cs2103t").build();

        assertNoChange(INDEX_FIRST_PERSON, descriptor);
    }

    @Test
    public void execute_removeAndAddSameContext_noChange() {
        // the context is removed and added back with the same spelling
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withContextsToRemove("CS2103T").withContextsToAdd("CS2103T").build();

        assertNoChange(INDEX_FIRST_PERSON, descriptor);
    }

    @Test
    public void execute_fixNameCapitalisation_success() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName("alice pauline").build();
        Person editedAlice = new PersonBuilder(ALICE).withName("alice pauline").build();

        assertEditSuccess(INDEX_FIRST_PERSON, descriptor, editedAlice);
    }

    @Test
    public void execute_fixEmailCapitalisation_success() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withEmail("Alice@Example.com").build();
        Person editedAlice = new PersonBuilder(ALICE).withEmail("Alice@Example.com").build();

        assertEditSuccess(INDEX_FIRST_PERSON, descriptor, editedAlice);
        // emails match ignoring case, so check the saved spelling directly
        assertEquals("Alice@Example.com",
                model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased()).getEmail().get().value);
    }

    @Test
    public void execute_unmatchedContextToRemove_failure() {
        // "CS2130T" is a typo of Alice's "CS2103T"
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withContextsToRemove("CS2130T").build());

        assertCommandFailure(editCommand, model,
                "Alice Pauline does not have context(s): [CS2130T]. Check the spelling.");
    }

    @Test
    public void execute_removeLastContext_failure() {
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withContextsToRemove("CS2103T").build());

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_NO_CONTEXT_LEFT);
    }

    @Test
    public void execute_duplicatePersonUnfilteredList_failure() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(firstPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_SECOND_PERSON, descriptor);

        assertCommandFailure(editCommand, model,
                "This person already exists: Alice Pauline has the same phone number (94351253).");
    }

    @Test
    public void execute_duplicatePersonFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        // edit person in filtered list into a duplicate in address book
        Person personInList = model.getAddressBook().getPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder(personInList).build());

        assertCommandFailure(editCommand, model,
                "This person already exists: Benson Meier has the same phone number (98765432).");
    }

    @Test
    public void execute_duplicateEmailKeepingOwnPhone_failure() {
        // keeps Alice's own phone but takes Benson's email
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withEmail("johnd@example.com").build());

        assertCommandFailure(editCommand, model,
                "This person already exists: Benson Meier has the same email (johnd@example.com).");
    }

    @Test
    public void execute_samePhoneAsBefore_noChange() {
        // the person being edited is not counted as a duplicate of itself
        assertNoChange(INDEX_FIRST_PERSON, new EditPersonDescriptorBuilder().withPhone("94351253").build());
    }

    @Test
    public void execute_removePhone_success() {
        // Alice keeps her email and contexts
        Person editedAlice = new PersonBuilder(ALICE).withoutPhone().build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withPhoneRemoved().build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedAlice));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(ALICE, editedAlice);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_removePhoneAndEmail_success() {
        // a person may end up with neither phone nor email
        Person editedAlice = new PersonBuilder(ALICE).withoutPhone().withoutEmail().build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withPhoneRemoved().withEmailRemoved().build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedAlice));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(ALICE, editedAlice);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_removePhoneAndEmailWhenOthersHaveNeither_success() {
        // two people without a phone or email are not duplicates of each other
        Person bensonWithoutPhoneAndEmail = new PersonBuilder(BENSON).withoutPhone().withoutEmail().build();
        model.setPerson(BENSON, bensonWithoutPhoneAndEmail);

        Person editedAlice = new PersonBuilder(ALICE).withoutPhone().withoutEmail().build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withPhoneRemoved().withEmailRemoved().build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedAlice));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(ALICE, editedAlice);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_removeMissingPhone_noChange() {
        // removing a phone the person does not have is accepted and changes nothing
        Person aliceWithoutPhone = new PersonBuilder(ALICE).withoutPhone().build();
        model.setPerson(ALICE, aliceWithoutPhone);

        assertNoChange(INDEX_FIRST_PERSON, new EditPersonDescriptorBuilder().withPhoneRemoved().build());
    }

    @Test
    public void execute_removeMissingEmail_noChange() {
        Person aliceWithoutEmail = new PersonBuilder(ALICE).withoutEmail().build();
        model.setPerson(ALICE, aliceWithoutEmail);

        assertNoChange(INDEX_FIRST_PERSON, new EditPersonDescriptorBuilder().withEmailRemoved().build());
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of address book
     */
    @Test
    public void execute_invalidPersonIndexFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        EditCommand editCommand = new EditCommand(outOfBoundIndex,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final EditCommand standardCommand = new EditCommand(INDEX_FIRST_PERSON, DESC_AMY);

        // same values -> returns true
        EditPersonDescriptor copyDescriptor = new EditPersonDescriptor(DESC_AMY);
        EditCommand commandWithSameValues = new EditCommand(INDEX_FIRST_PERSON, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_SECOND_PERSON, DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_FIRST_PERSON, DESC_BOB)));
    }

    @Test
    public void toStringMethod() {
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, editPersonDescriptor);
        String expected = EditCommand.class.getCanonicalName() + "{index=" + INDEX_FIRST_PERSON
                + ", editPersonDescriptor=" + editPersonDescriptor + "}";
        assertEquals(expected, editCommand.toString());
    }

    /**
     * Executes an edit of the person at {@code index} with {@code descriptor} and checks that
     * the person is replaced by {@code editedPerson}.
     */
    private void assertEditSuccess(Index index, EditPersonDescriptor descriptor, Person editedPerson) {
        Person personToEdit = model.getFilteredPersonList().get(index.getZeroBased());
        EditCommand editCommand = new EditCommand(index, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personToEdit, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    /**
     * Executes an edit of the person at {@code index} with {@code descriptor} and checks that
     * nothing changes and the result says so.
     */
    private void assertNoChange(Index index, EditPersonDescriptor descriptor) {
        Person personToEdit = model.getFilteredPersonList().get(index.getZeroBased());
        EditCommand editCommand = new EditCommand(index, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_NO_CHANGES, personToEdit.getName());

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

}
