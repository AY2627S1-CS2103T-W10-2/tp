package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    public static final String MESSAGE_CONTEXT_REQUIRED = "A person needs at least one context";

    // Identity fields
    private final Name name;
    private final Optional<Phone> phone;
    private final Optional<Email> email;

    // Data fields
    private final Set<Context> contexts = new HashSet<>();
    private final Optional<Alias> alias;
    private final Optional<Introducer> introducer;
    private final Optional<Note> note;
    private final boolean archived;

    /**
     * Creates a person without the additional optional details.
     * {@code phone} and {@code email} may be null if absent.
     *
     * @throws NullPointerException if {@code contexts} contains a null element.
     * @throws IllegalArgumentException if {@code contexts} is empty.
     */
    public Person(Name name, Phone phone, Email email, Set<Context> contexts) {
        this(name, phone, email, contexts, null, null, null);
    }

    /**
     * Creates a person with optional alias, introducer, and note details.
     * The optional arguments may be null if absent; name and contexts must be present.
     *
     * @throws IllegalArgumentException if {@code contexts} is empty.
     */
    public Person(Name name, Phone phone, Email email, Set<Context> contexts,
            Alias alias, Introducer introducer, Note note) {
        this(name, phone, email, contexts, alias, introducer, note, false);
    }

    /**
     * Creates a person with optional alias, introducer, and note details, and says whether the person is archived.
     * The optional arguments may be null if absent; name and contexts must be present.
     * An archived person is kept in the address book but is left out of the normal contact list.
     *
     * @throws NullPointerException if {@code contexts} contains a null element.
     * @throws IllegalArgumentException if {@code contexts} is empty.
     */
    public Person(Name name, Phone phone, Email email, Set<Context> contexts,
            Alias alias, Introducer introducer, Note note, boolean archived) {
        requireAllNonNull(name, contexts);
        requireAllNonNull(contexts);
        if (contexts.isEmpty()) {
            throw new IllegalArgumentException(MESSAGE_CONTEXT_REQUIRED);
        }
        this.name = name;
        this.phone = Optional.ofNullable(phone);
        this.email = Optional.ofNullable(email);
        this.contexts.addAll(contexts);
        this.alias = Optional.ofNullable(alias);
        this.introducer = Optional.ofNullable(introducer);
        this.note = Optional.ofNullable(note);
        this.archived = archived;
    }

    public Name getName() {
        return name;
    }

    public Optional<Phone> getPhone() {
        return phone;
    }

    public Optional<Email> getEmail() {
        return email;
    }

    public Optional<Alias> getAlias() {
        return alias;
    }

    public Optional<Introducer> getIntroducer() {
        return introducer;
    }

    public Optional<Note> getNote() {
        return note;
    }

    public boolean isArchived() {
        return archived;
    }

    /**
     * Returns a copy of this person with every detail unchanged except for the archived flag.
     */
    public Person withArchived(boolean archived) {
        return new Person(name, phone.orElse(null), email.orElse(null), contexts,
                alias.orElse(null), introducer.orElse(null), note.orElse(null), archived);
    }

    /**
     * Returns an immutable context set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Context> getContexts() {
        return Collections.unmodifiableSet(contexts);
    }

    /**
     * Returns true if both persons share a phone number or an email.
     * A missing phone or email never matches another missing one, and the name is not considered.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && ((phone.isPresent() && phone.equals(otherPerson.phone))
                || (email.isPresent() && email.equals(otherPerson.email)));
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person otherPerson)) {
            return false;
        }

        return name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && contexts.equals(otherPerson.contexts)
                && alias.equals(otherPerson.alias)
                && introducer.equals(otherPerson.introducer)
                && note.equals(otherPerson.note)
                && archived == otherPerson.archived;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, phone, email, contexts, alias, introducer, note, archived);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone.orElse(null))
                .add("email", email.orElse(null))
                .add("contexts", contexts)
                .add("alias", alias.orElse(null))
                .add("introducer", introducer.orElse(null))
                .add("note", note.orElse(null))
                .add("archived", archived)
                .toString();
    }

}
