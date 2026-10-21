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

import java.util.HashSet;
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
    public void additionalDetails_absentAndPresent() {
        assertTrue(ALICE.getAlias().isEmpty());
        assertTrue(ALICE.getIntroducer().isEmpty());
        assertTrue(ALICE.getNote().isEmpty());

        Person withDetails = new PersonBuilder(ALICE).withAlias("Ally")
                .withIntroducer("Ben Lee").withNote("Met at orientation").build();
        assertEquals(new Alias("Ally"), withDetails.getAlias().orElseThrow());
        assertEquals(new Introducer("Ben Lee"), withDetails.getIntroducer().orElseThrow());
        assertEquals(new Note("Met at orientation"), withDetails.getNote().orElseThrow());
        assertEquals(withDetails, new PersonBuilder(withDetails).build());
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
    public void constructor_nullElementInContexts_throwsNullPointerException() {
        Set<Context> withNull = new HashSet<>();
        withNull.add(new Context("CS2103T"));
        withNull.add(null);
        assertThrows(NullPointerException.class, () -> new Person(ALICE.getName(),
                ALICE.getPhone().orElse(null), ALICE.getEmail().orElse(null), withNull));
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

        // additional details never affect duplicate detection
        Person withAdditionalDetails = new PersonBuilder(ALICE).withAlias("Ally")
                .withIntroducer("Ben Lee").withNote("Met at orientation").build();
        assertTrue(ALICE.isSamePerson(withAdditionalDetails));
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

        // different additional details -> returns false
        assertFalse(ALICE.equals(new PersonBuilder(ALICE).withAlias("Ally").build()));
        assertFalse(ALICE.equals(new PersonBuilder(ALICE).withIntroducer("Ben Lee").build()));
        assertFalse(ALICE.equals(new PersonBuilder(ALICE).withNote("Met at orientation").build()));

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
                + ", contexts=" + ALICE.getContexts()
                + ", alias=null, introducer=null, note=null, archived=false}";
        assertEquals(expected, ALICE.toString());
    }

    @Test
    public void isArchived_defaultsToFalse() {
        assertFalse(ALICE.isArchived());
    }

    @Test
    public void withArchived_changesOnlyTheFlag() {
        Person full = new PersonBuilder(ALICE).withAlias("Ally").withIntroducer("Bob Lee")
                .withNote("Met at demo").build();

        Person archived = full.withArchived(true);

        assertTrue(archived.isArchived());
        assertEquals(full, archived.withArchived(false));
        assertEquals(full.getAlias(), archived.getAlias());
        assertEquals(full.getIntroducer(), archived.getIntroducer());
        assertEquals(full.getNote(), archived.getNote());
    }

    @Test
    public void equals_archivedFlagMatters() {
        assertFalse(ALICE.equals(ALICE.withArchived(true)));
        assertEquals(ALICE.withArchived(true), new PersonBuilder(ALICE).withArchived(true).build());
        Person other = new PersonBuilder(ALICE).withArchived(true).build();
        assertEquals(ALICE.withArchived(true).hashCode(), other.hashCode());
    }

    @Test
    public void isSamePerson_ignoresArchivedFlag() {
        assertTrue(ALICE.isSamePerson(ALICE.withArchived(true)));
    }
}
