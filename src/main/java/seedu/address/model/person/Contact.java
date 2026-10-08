package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents a Contact in UniContacts.
 * Reference design for the new contact card; not yet used by the rest of the app.
 * Guarantees: name and at least one context are present, field values are validated, immutable.
 * The phone and email are optional.
 */
public class Contact {

    public static final String MESSAGE_CONTEXT_REQUIRED = "A contact needs at least one context";

    private final Name name;
    private final Phone phone; // null if absent; exposed through Optional
    private final Email email; // null if absent; exposed through Optional
    private final Set<Context> contexts = new HashSet<>();

    /**
     * Name and contexts must be present and not null. Phone and email may be null if absent.
     * {@code contexts} must contain at least one context.
     */
    public Contact(Name name, Phone phone, Email email, Set<Context> contexts) {
        requireAllNonNull(name, contexts);
        if (contexts.isEmpty()) {
            throw new IllegalArgumentException(MESSAGE_CONTEXT_REQUIRED);
        }
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.contexts.addAll(contexts);
    }

    /**
     * Creates a contact with no phone and no email.
     */
    public Contact(Name name, Set<Context> contexts) {
        this(name, null, null, contexts);
    }

    public Name getName() {
        return name;
    }

    public Optional<Phone> getPhone() {
        return Optional.ofNullable(phone);
    }

    public Optional<Email> getEmail() {
        return Optional.ofNullable(email);
    }

    /**
     * Returns an immutable context set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Context> getContexts() {
        return Collections.unmodifiableSet(contexts);
    }

    /**
     * Returns true if both contacts share a phone number or an email.
     * A missing phone or email never matches another missing one.
     */
    public boolean isSameContact(Contact otherContact) {
        if (otherContact == this) {
            return true;
        }

        return otherContact != null
                && ((phone != null && phone.equals(otherContact.phone))
                || (email != null && email.equals(otherContact.email)));
    }

    /**
     * Returns true if both contacts have the same name, phone, email and contexts.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Contact otherContact)) {
            return false;
        }

        return name.equals(otherContact.name)
                && Objects.equals(phone, otherContact.phone)
                && Objects.equals(email, otherContact.email)
                && contexts.equals(otherContact.contexts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, phone, email, contexts);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("contexts", contexts)
                .toString();
    }
}
