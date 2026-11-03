package seedu.address.model;

import java.util.Optional;
import java.util.function.Predicate;

import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.person.Person;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluates to true */
    Predicate<Person> PREDICATE_SHOW_ALL_PERSONS = unused -> true;

    /** {@code Predicate} that shows the persons who are not archived. This is the normal person list. */
    Predicate<Person> PREDICATE_SHOW_ACTIVE = person -> !person.isArchived();

    /** {@code Predicate} that shows only the archived persons. */
    Predicate<Person> PREDICATE_SHOW_ARCHIVED = Person::isArchived;

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Replaces address book data with the data in {@code addressBook}.
     */
    void setAddressBook(ReadOnlyAddressBook addressBook);

    /** Returns the AddressBook */
    ReadOnlyAddressBook getAddressBook();

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    boolean hasPerson(Person person);

    /**
     * Returns the first person with the same identity as {@code person} in the address book,
     * or an empty {@code Optional} if there is none.
     */
    Optional<Person> findDuplicate(Person person);

    /**
     * Returns the first person other than {@code toSkip} with the same identity as {@code toCheck}
     * in the address book, or an empty {@code Optional} if there is none.
     */
    Optional<Person> findDuplicate(Person toCheck, Person toSkip);

    /**
     * Deletes the given person.
     * The person must exist in the address book.
     */
    void deletePerson(Person target);

    /**
     * Adds the given person.
     * {@code person} must not already exist in the address book.
     */
    void addPerson(Person person);

    /**
     * Replaces the given person {@code target} with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    void setPerson(Person target, Person editedPerson);

    /** Returns an unmodifiable view of the filtered person list */
    ObservableList<Person> getFilteredPersonList();

    /**
     * Updates the filter of the filtered person list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPersonList(Predicate<Person> predicate);

    /**
     * Returns a value that is true while the filtered person list is the list of archived persons,
     * that is, after the predicate was set to {@code PREDICATE_SHOW_ARCHIVED}.
     */
    ObservableBooleanValue showingArchivesProperty();
}
