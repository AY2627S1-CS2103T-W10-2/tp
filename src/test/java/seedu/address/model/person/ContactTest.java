package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

public class ContactTest {

    private static final Name NAME = new Name("Amy Bee");
    private static final Phone PHONE = new Phone("91234567");
    private static final Email EMAIL = new Email("amy@example.com");
    private static final Set<Context> CONTEXTS = Set.of(new Context("CS2103T"));

    @Test
    public void constructor_nullName_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Contact(null, PHONE, EMAIL, CONTEXTS));
    }

    @Test
    public void constructor_noContexts_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Contact(NAME, PHONE, EMAIL, Set.of()));
    }

    @Test
    public void optionalPhoneAndEmail() {
        Contact withBoth = new Contact(NAME, PHONE, EMAIL, CONTEXTS);
        assertEquals(PHONE, withBoth.getPhone().get());
        assertEquals(EMAIL, withBoth.getEmail().get());

        Contact withNeither = new Contact(NAME, CONTEXTS);
        assertTrue(withNeither.getPhone().isEmpty());
        assertTrue(withNeither.getEmail().isEmpty());
    }

    @Test
    public void getContexts_modify_throwsUnsupportedOperationException() {
        Contact contact = new Contact(NAME, CONTEXTS);
        assertThrows(UnsupportedOperationException.class, () ->
                contact.getContexts().remove(new Context("CS2103T")));
    }

    @Test
    public void contexts_differingOnlyInCase_areEqual() {
        Contact contact = new Contact(NAME, Set.of(new Context("CS2103T")));
        assertEquals(contact, new Contact(NAME, Set.of(new Context("cs2103t"))));
    }

    @Test
    public void isSameContact() {
        Contact contact = new Contact(NAME, PHONE, EMAIL, CONTEXTS);

        // same phone, different everything else
        assertTrue(contact.isSameContact(new Contact(new Name("Other"), PHONE, null, CONTEXTS)));
        // same email only
        assertTrue(contact.isSameContact(new Contact(new Name("Other"), null, EMAIL, CONTEXTS)));
        // same name but different phone and email
        assertFalse(contact.isSameContact(
                new Contact(NAME, new Phone("98765432"), new Email("x@example.com"), CONTEXTS)));
        // two contacts with no phone and no email are not duplicates
        assertFalse(new Contact(NAME, CONTEXTS).isSameContact(new Contact(new Name("Other"), CONTEXTS)));
        assertFalse(contact.isSameContact(null));
    }

    @Test
    public void equals() {
        Contact contact = new Contact(NAME, PHONE, EMAIL, CONTEXTS);
        assertEquals(contact, new Contact(NAME, PHONE, EMAIL, CONTEXTS));
        assertNotEquals(contact, new Contact(NAME, CONTEXTS));
        assertNotEquals(contact, new Contact(new Name("Other"), PHONE, EMAIL, CONTEXTS));
        assertNotEquals(contact, new Contact(NAME, PHONE, EMAIL, Set.of(new Context("CS2101"))));
    }
}
