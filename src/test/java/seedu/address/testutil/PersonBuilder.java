package seedu.address.testutil;

import java.util.HashSet;
import java.util.Set;

import seedu.address.model.person.Alias;
import seedu.address.model.person.Context;
import seedu.address.model.person.Email;
import seedu.address.model.person.Introducer;
import seedu.address.model.person.Name;
import seedu.address.model.person.Note;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

/**
 * A utility class to help with building Person objects.
 */
public class PersonBuilder {

    public static final String DEFAULT_NAME = "Amy Bee";
    public static final String DEFAULT_PHONE = "85355255";
    public static final String DEFAULT_EMAIL = "amy@gmail.com";
    public static final String DEFAULT_CONTEXT = "CS2103T";

    private Name name;
    private Phone phone;
    private Email email;
    private Set<Context> contexts;
    private Alias alias;
    private Introducer introducer;
    private Note note;
    private boolean archived;

    /**
     * Creates a {@code PersonBuilder} with the default details.
     */
    public PersonBuilder() {
        name = new Name(DEFAULT_NAME);
        phone = new Phone(DEFAULT_PHONE);
        email = new Email(DEFAULT_EMAIL);
        contexts = new HashSet<>(Set.of(new Context(DEFAULT_CONTEXT)));
    }

    /**
     * Initializes the PersonBuilder with the data of {@code personToCopy}.
     */
    public PersonBuilder(Person personToCopy) {
        name = personToCopy.getName();
        phone = personToCopy.getPhone().orElse(null);
        email = personToCopy.getEmail().orElse(null);
        contexts = new HashSet<>(personToCopy.getContexts());
        alias = personToCopy.getAlias().orElse(null);
        introducer = personToCopy.getIntroducer().orElse(null);
        note = personToCopy.getNote().orElse(null);
        archived = personToCopy.isArchived();
    }

    /**
     * Sets the {@code Name} of the {@code Person} that we are building.
     */
    public PersonBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Parses the {@code contexts} into a {@code Set<Context>} and sets it to the {@code Person} being built.
     */
    public PersonBuilder withContexts(String ... contexts) {
        this.contexts = new HashSet<>();
        for (String context : contexts) {
            this.contexts.add(new Context(context));
        }
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code Person} that we are building.
     */
    public PersonBuilder withPhone(String phone) {
        this.phone = new Phone(phone);
        return this;
    }

    /**
     * Removes the {@code Phone} of the {@code Person} that we are building.
     */
    public PersonBuilder withoutPhone() {
        this.phone = null;
        return this;
    }

    /**
     * Removes the {@code Email} of the {@code Person} that we are building.
     */
    public PersonBuilder withoutEmail() {
        this.email = null;
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code Person} that we are building.
     */
    public PersonBuilder withEmail(String email) {
        this.email = new Email(email);
        return this;
    }

    /**
     * Sets the optional alias of the person being built.
     */
    public PersonBuilder withAlias(String alias) {
        this.alias = new Alias(alias);
        return this;
    }

    /**
     * Sets the optional introducer name of the person being built.
     */
    public PersonBuilder withIntroducer(String introducer) {
        this.introducer = new Introducer(introducer);
        return this;
    }

    /**
     * Sets the optional personal note of the person being built.
     */
    public PersonBuilder withNote(String note) {
        this.note = new Note(note);
        return this;
    }

    /**
     * Sets whether the person being built is archived.
     */
    public PersonBuilder withArchived(boolean archived) {
        this.archived = archived;
        return this;
    }

    public Person build() {
        return new Person(name, phone, email, contexts, alias, introducer, note, archived);
    }

}
