package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Context;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class MessagesTest {

    @Test
    public void formatContexts_multipleContexts_keepsGivenOrderInBrackets() {
        assertEquals("[b] [A]", Messages.formatContexts(List.of(new Context("b"), new Context("A"))));
    }

    @Test
    public void formatContexts_singleContext_showsContextInBrackets() {
        assertEquals("[A]", Messages.formatContexts(List.of(new Context("A"))));
    }

    @Test
    public void formatContextsSorted_multipleContexts_sortedCaseInsensitivelyInBrackets() {
        assertEquals("[A] [b] [c]",
                Messages.formatContextsSorted(List.of(new Context("b"), new Context("A"), new Context("c"))));
    }

    @Test
    public void format_allFields_showsAllFields() {
        Person person = new PersonBuilder().build();
        assertEquals("Amy Bee; Contexts: [CS2103T]; Phone: 85355255; Email: amy@gmail.com",
                Messages.format(person));
    }

    @Test
    public void format_noPhone_leavesOutPhone() {
        Person person = new PersonBuilder().withoutPhone().build();
        assertEquals("Amy Bee; Contexts: [CS2103T]; Email: amy@gmail.com", Messages.format(person));
    }

    @Test
    public void format_noEmail_leavesOutEmail() {
        Person person = new PersonBuilder().withoutEmail().build();
        assertEquals("Amy Bee; Contexts: [CS2103T]; Phone: 85355255", Messages.format(person));
    }

    @Test
    public void format_noPhoneAndNoEmail_leavesOutBoth() {
        Person person = new PersonBuilder().withoutPhone().withoutEmail().build();
        assertEquals("Amy Bee; Contexts: [CS2103T]", Messages.format(person));
    }

    @Test
    public void format_multipleContexts_sortedCaseInsensitivelyInBrackets() {
        Person person = new PersonBuilder().withContexts("hiking club", "CS2103T", "Hack&Roll 2026").build();
        assertEquals("Amy Bee; Contexts: [CS2103T] [Hack&Roll 2026] [hiking club]; Phone: 85355255; "
                + "Email: amy@gmail.com", Messages.format(person));
    }

    @Test
    public void format_additionalDetails_showsOnlyPresentDetails() {
        Person person = new PersonBuilder().withAlias("May").withIntroducer("Alex Tan")
                .withNote("Met at Hack&Roll").build();
        assertEquals("Amy Bee; Contexts: [CS2103T]; Phone: 85355255; Email: amy@gmail.com; "
                + "Alias: May; Introduced by: Alex Tan; Note: Met at Hack&Roll", Messages.format(person));
    }

    @Test
    public void getErrorMessageForDuplicatePerson_activePerson_hasNoArchivedHint() {
        Person person = new PersonBuilder().build();
        assertEquals("This person already exists: Amy Bee has the same phone number (85355255).",
                Messages.getErrorMessageForDuplicatePerson(person, person));
    }

    @Test
    public void getErrorMessageForDuplicatePerson_archivedPhoneClash_addsArchivedHint() {
        Person archived = new PersonBuilder().withArchived(true).build();
        assertEquals("This person already exists: Amy Bee has the same phone number (85355255)."
                + " This person is archived; run archives to see them.",
                Messages.getErrorMessageForDuplicatePerson(archived, archived));
    }

    @Test
    public void getErrorMessageForDuplicatePerson_archivedEmailClash_addsArchivedHint() {
        Person archived = new PersonBuilder().withoutPhone().withArchived(true).build();
        assertEquals("This person already exists: Amy Bee has the same email (amy@gmail.com)."
                + " This person is archived; run archives to see them.",
                Messages.getErrorMessageForDuplicatePerson(archived, archived));
    }
}
