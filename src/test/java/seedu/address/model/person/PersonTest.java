package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CONTEXT_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class PersonTest {

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Person person = new PersonBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> person.getContexts().remove(0));
    }

    @Test
    public void getContexts_modify_throwsUnsupportedOperationException() {
        Person person = new PersonBuilder().withContexts("CS2103T").build();
        assertThrows(UnsupportedOperationException.class, () -> person.getContexts().remove(new Context("CS2103T")));
    }

    @Test
    public void getPhone_present_returnsPhone() {
        assertTrue(ALICE.getPhone().isPresent());
    }

    @Test
    public void getPhone_absent_returnsEmptyOptional() {
        Person withoutPhone = new PersonBuilder(ALICE).withoutPhone().build();
        assertTrue(withoutPhone.getPhone().isEmpty());
    }

    @Test
    public void getEmail_absent_returnsEmptyOptional() {
        Person withoutEmail = new PersonBuilder(ALICE).withoutEmail().build();
        assertTrue(withoutEmail.getEmail().isEmpty());
    }

    @Test
    public void equals_phoneAbsentVersusPresent() {
        Person withoutPhone = new PersonBuilder(ALICE).withoutPhone().build();
        assertFalse(ALICE.equals(withoutPhone));
        assertTrue(withoutPhone.equals(new PersonBuilder(ALICE).withoutPhone().build()));
        assertEquals(withoutPhone.hashCode(), new PersonBuilder(ALICE).withoutPhone().build().hashCode());
    }

    @Test
    public void constructor_emptyContexts_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, Person.MESSAGE_CONTEXT_REQUIRED, () -> new Person(
                ALICE.getName(), ALICE.getPhone().orElse(null), ALICE.getEmail().orElse(null), Set.of()));
    }

    @Test
    public void constructor_nullContexts_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Person(
                ALICE.getName(), ALICE.getPhone().orElse(null), ALICE.getEmail().orElse(null), null));
    }

    @Test
    public void isSamePerson() {
        // same object -> returns true
        assertTrue(ALICE.isSamePerson(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSamePerson(null));

        // same phone, everything else different -> returns true
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).withEmail(VALID_EMAIL_BOB)
                .withContexts("Hiking club").build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // same email, everything else different -> returns true
        editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .withContexts("Hiking club").build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // same name, different phone and email -> returns false
        editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.isSamePerson(editedAlice));

        // both have no phone and no email -> returns false
        Person noDetails = new PersonBuilder(ALICE).withoutPhone().withoutEmail().build();
        assertFalse(noDetails.isSamePerson(new PersonBuilder(BOB).withoutPhone().withoutEmail().build()));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Person aliceCopy = new PersonBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different person -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different email -> returns false
        editedAlice = new PersonBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different contexts -> returns false
        editedAlice = new PersonBuilder(ALICE).withContexts(VALID_CONTEXT_HUSBAND).build();
        assertFalse(ALICE.equals(editedAlice));

        // contexts differing only in case -> returns true
        Person personWithContext = new PersonBuilder(ALICE).withContexts("CS2103T").build();
        Person personWithSameContext = new PersonBuilder(ALICE).withContexts("cs2103t").build();
        assertEquals(personWithContext, personWithSameContext);
        assertEquals(personWithContext.hashCode(), personWithSameContext.hashCode());
    }

    @Test
    public void toStringMethod() {
        String expected = Person.class.getCanonicalName() + "{name=" + ALICE.getName()
                + ", phone=" + ALICE.getPhone().orElse(null)
                + ", email=" + ALICE.getEmail().orElse(null)
                + ", contexts=" + ALICE.getContexts() + "}";
        assertEquals(expected, ALICE.toString());
    }
}
