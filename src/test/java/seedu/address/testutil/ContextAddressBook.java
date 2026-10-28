package seedu.address.testutil;

import seedu.address.model.AddressBook;
import seedu.address.model.person.Person;

/**
 * An address book whose persons have different mixes of contexts, for tests that act on a single context.
 */
public class ContextAddressBook {

    public static final String CS2103T = "CS2103T";
    public static final String HIKING = "Hiking club";
    public static final String CHESS = "Chess";

    public static final Person ONLY_CS2103T = new PersonBuilder().withName("Anna Lim").withPhone("91111111")
            .withEmail("anna@example.com").withContexts(CS2103T).build();
    public static final Person CS2103T_AND_HIKING = new PersonBuilder().withName("Ben Tan").withPhone("92222222")
            .withEmail("ben@example.com").withContexts(CS2103T, HIKING).build();
    public static final Person ONLY_HIKING = new PersonBuilder().withName("Cara Ng").withPhone("93333333")
            .withEmail("cara@example.com").withContexts(HIKING).build();
    public static final Person ONLY_CHESS = new PersonBuilder().withName("Dev Rao").withPhone("94444444")
            .withEmail("dev@example.com").withContexts(CHESS).build();
    public static final Person ANOTHER_CS2103T = new PersonBuilder().withName("Eli Foo").withPhone("95555555")
            .withEmail("eli@example.com").withContexts(CS2103T).build();

    private ContextAddressBook() {}

    /** Returns an address book with the five persons above, none of them archived. */
    public static AddressBook get() {
        AddressBook ab = new AddressBook();
        ab.addPerson(ONLY_CS2103T);
        ab.addPerson(CS2103T_AND_HIKING);
        ab.addPerson(ONLY_HIKING);
        ab.addPerson(ONLY_CHESS);
        ab.addPerson(ANOTHER_CS2103T);
        return ab;
    }
}
