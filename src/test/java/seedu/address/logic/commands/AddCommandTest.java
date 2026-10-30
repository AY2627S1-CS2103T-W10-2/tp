package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class AddCommandTest {

    @Test
    public void constructor_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddCommand(null));
    }

    @Test
    public void execute_personAcceptedByModel_addSuccessful() throws Exception {
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded();
        Person validPerson = new PersonBuilder().build();

        CommandResult commandResult = new AddCommand(validPerson).execute(modelStub);

        assertEquals(String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPerson)),
                commandResult.getFeedbackToUser());
        assertEquals(List.of(validPerson), modelStub.personsAdded);
    }

    @Test
    public void execute_noExistingSimilarPerson_noNote() throws Exception {
        Model model = new ModelManager();
        Person toAdd = person("Wei Jie", false, "CS2103T");

        assertEquals(successMessage(toAdd), new AddCommand(toAdd).execute(model).getFeedbackToUser());
    }

    @Test
    public void execute_sharedContext_addedWithActiveNote() throws Exception {
        String note = "\nNote: Wei Jie [CS2103T] already exists at index 1. "
                + "If this is the same person, run delete 2 to remove the new entry and use edit 1 instead.";

        // exact same contexts, so the new person equals the existing one but is still shown at index 2
        assertNoteAfterAdding(person("Wei Jie", false, "CS2103T"), person("Wei Jie", false, "CS2103T"), note);
        // new person has a subset of the existing contexts
        assertNoteAfterAdding(person("Wei Jie", false, "CS2103T", "Hiking club"),
                person("Wei Jie", false, "CS2103T"), note);
        // new person has a superset of the existing contexts
        assertNoteAfterAdding(person("Wei Jie", false, "CS2103T"),
                person("Wei Jie", false, "CS2103T", "Hiking club"), note);
        // partial overlap, only the shared context is listed
        assertNoteAfterAdding(person("Wei Jie", false, "CS2103T", "Chess"),
                person("Wei Jie", false, "CS2103T", "Hiking club"), note);
        // name differs by case and spaces, context by case
        assertNoteAfterAdding(person("wei  jie", false, "cs2103t"), person("Wei Jie", false, "CS2103T"),
                "\nNote: wei  jie [cs2103t] already exists at index 1. "
                        + "If this is the same person, run delete 2 to remove the new entry and use edit 1 instead.");
    }

    @Test
    public void execute_existingPersonHasPhone_addedWithNote() throws Exception {
        Person existing = new PersonBuilder().withName("Wei Jie").withContexts("CS2103T")
                .withPhone("91234567").withoutEmail().build();

        assertNoteAfterAdding(existing, person("Wei Jie", false, "CS2103T"),
                "\nNote: Wei Jie [CS2103T] already exists at index 1. "
                        + "If this is the same person, run delete 2 to remove the new entry and use edit 1 instead.");
    }

    @Test
    public void execute_noSharedContextOrDifferentName_noNote() throws Exception {
        Person existing = person("Wei Jie", false, "CS2103T");

        assertNoteAfterAdding(existing, person("Wei Jie", false, "CS2101"), "");
        assertNoteAfterAdding(existing, person("Wei Jian", false, "CS2103T"), "");
    }

    @Test
    public void execute_newPersonHasPhoneOrEmail_addedWithNote() throws Exception {
        String note = "\nNote: Wei Jie [CS2103T] already exists at index 1. "
                + "If this is the same person, run delete 2 to remove the new entry and use edit 1 instead.";
        Person withPhone = new PersonBuilder().withName("Wei Jie").withContexts("CS2103T")
                .withPhone("91234567").withoutEmail().build();
        Person withEmail = new PersonBuilder().withName("Wei Jie").withContexts("CS2103T")
                .withEmail("weijie@example.com").withoutPhone().build();

        // existing person has no phone or email
        assertNoteAfterAdding(person("Wei Jie", false, "CS2103T"), withPhone, note);
        assertNoteAfterAdding(person("Wei Jie", false, "CS2103T"), withEmail, note);
        // existing person has a different phone or email
        assertNoteAfterAdding(withPhone, withEmail, note);
        assertNoteAfterAdding(withEmail, withPhone, note);
        assertNoteAfterAdding(withPhone, new PersonBuilder(withPhone).withPhone("98765432").build(), note);
        assertNoteAfterAdding(withEmail, new PersonBuilder(withEmail).withEmail("weijie@u.nus.edu").build(), note);
    }

    @Test
    public void execute_newPersonHasPhoneOrEmailAndArchivedSimilarPerson_addedWithArchivedNote() throws Exception {
        Person archived = new PersonBuilder().withName("Wei Jie").withContexts("CS2103T")
                .withPhone("91234567").withoutEmail().withArchived(true).build();
        Person withEmail = new PersonBuilder().withName("Wei Jie").withContexts("CS2103T")
                .withEmail("weijie@example.com").withoutPhone().build();

        assertNoteAfterAdding(archived, withEmail,
                "\nNote: Wei Jie [CS2103T] already exists."
                        + " This person is archived; run archives to see them.");
    }

    @Test
    public void execute_similarPersonWithSamePhoneOrEmail_throwsCommandException() {
        Person existing = new PersonBuilder().withName("Wei Jie").withContexts("CS2103T")
                .withPhone("91234567").withEmail("weijie@example.com").build();
        Model model = new ModelManager();
        model.addPerson(existing);

        // same name and context, so only the shared phone or email makes it a duplicate
        Person samePhone = new PersonBuilder(existing).withoutEmail().build();
        String samePhoneMessage = Messages.getErrorMessageForDuplicatePerson(existing, samePhone);
        assertThrows(CommandException.class, samePhoneMessage, () -> new AddCommand(samePhone).execute(model));
        Person sameEmail = new PersonBuilder(existing).withoutPhone().build();
        String sameEmailMessage = Messages.getErrorMessageForDuplicatePerson(existing, sameEmail);
        assertThrows(CommandException.class, sameEmailMessage, () -> new AddCommand(sameEmail).execute(model));
        assertEquals(1, model.getAddressBook().getPersonList().size());
    }

    @Test
    public void execute_archivedSimilarPerson_addedWithArchivedNote() throws Exception {
        assertNoteAfterAdding(person("Wei Jie", true, "CS2103T"), person("Wei Jie", false, "CS2103T"),
                "\nNote: Wei Jie [CS2103T] already exists."
                        + " This person is archived; run archives to see them.");
    }

    @Test
    public void execute_activeAndArchivedSimilarPersons_activeNoteWithIndex() throws Exception {
        Model model = new ModelManager();
        model.addPerson(person("Wei Jie", true, "CS2103T"));
        model.addPerson(person("Alex", false, "CS2103T"));
        model.addPerson(person("Wei Jie", false, "CS2103T", "Hiking club"));
        Person toAdd = person("Wei Jie", false, "CS2103T", "Hiking club");

        CommandResult result = new AddCommand(toAdd).execute(model);

        // the archived person is hidden, so the active match is at index 2 and the new person at index 3
        assertEquals(successMessage(toAdd) + "\nNote: Wei Jie [CS2103T] [Hiking club] already exists at "
                + "index 2. If this is the same person, run delete 3 to remove the new entry and use edit 2 instead.",
                result.getFeedbackToUser());
    }

    private static Person person(String name, boolean isArchived, String... contexts) {
        return new PersonBuilder().withName(name).withContexts(contexts).withoutPhone().withoutEmail()
                .withArchived(isArchived).build();
    }

    private static String successMessage(Person person) {
        return String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(person));
    }

    /**
     * Adds {@code existing} then executes an add of {@code toAdd}, and checks the feedback is the success
     * message followed by {@code expectedNote} (empty if no note is expected).
     */
    private static void assertNoteAfterAdding(Person existing, Person toAdd, String expectedNote)
            throws Exception {
        Model model = new ModelManager();
        model.addPerson(existing);
        CommandResult result = new AddCommand(toAdd).execute(model);
        assertEquals(successMessage(toAdd) + expectedNote, result.getFeedbackToUser());
    }

    @Test
    public void execute_duplicatePerson_throwsCommandException() {
        Person validPerson = new PersonBuilder().build();
        AddCommand addCommand = new AddCommand(validPerson);
        ModelStub modelStub = new ModelStubWithPerson(validPerson);

        String expectedMessage = Messages.getErrorMessageForDuplicatePerson(validPerson, validPerson);
        assertThrows(CommandException.class, expectedMessage, () -> addCommand.execute(modelStub));
    }

    @Test
    public void equals() {
        Person alice = new PersonBuilder().withName("Alice").build();
        Person bob = new PersonBuilder().withName("Bob").build();
        AddCommand addAliceCommand = new AddCommand(alice);
        AddCommand addBobCommand = new AddCommand(bob);

        // same object -> returns true
        assertTrue(addAliceCommand.equals(addAliceCommand));

        // same values -> returns true
        AddCommand addAliceCommandCopy = new AddCommand(alice);
        assertTrue(addAliceCommand.equals(addAliceCommandCopy));

        // different types -> returns false
        assertFalse(addAliceCommand.equals(1));

        // null -> returns false
        assertFalse(addAliceCommand.equals(null));

        // different person -> returns false
        assertFalse(addAliceCommand.equals(addBobCommand));
    }

    @Test
    public void toStringMethod() {
        AddCommand addCommand = new AddCommand(ALICE);
        String expected = AddCommand.class.getCanonicalName() + "{toAdd=" + ALICE + "}";
        assertEquals(expected, addCommand.toString());
    }

    /**
     * A default model stub that has all of the methods failing.
     */
    private class ModelStub implements Model {
        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBook(ReadOnlyAddressBook newData) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Optional<Person> findDuplicate(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Optional<Person> findDuplicate(Person toCheck, Person toSkip) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deletePerson(Person target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setPerson(Person target, Person editedPerson) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredPersonList(Predicate<Person> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableBooleanValue showingArchivesProperty() {
            throw new AssertionError("This method should not be called.");
        }
    }

    /**
     * A Model stub that contains a single person.
     */
    private class ModelStubWithPerson extends ModelStub {
        private final Person person;

        ModelStubWithPerson(Person person) {
            requireNonNull(person);
            this.person = person;
        }

        @Override
        public Optional<Person> findDuplicate(Person person) {
            requireNonNull(person);
            return Optional.of(this.person).filter(person::isSamePerson);
        }
    }

    /**
     * A Model stub that always accepts the person being added.
     */
    private class ModelStubAcceptingPersonAdded extends ModelStub {
        final ArrayList<Person> personsAdded = new ArrayList<>();

        @Override
        public Optional<Person> findDuplicate(Person person) {
            requireNonNull(person);
            return personsAdded.stream().filter(person::isSamePerson).findFirst();
        }

        @Override
        public void addPerson(Person person) {
            requireNonNull(person);
            personsAdded.add(person);
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            AddressBook addressBook = new AddressBook();
            addressBook.setPersons(personsAdded);
            return addressBook;
        }
    }

}
