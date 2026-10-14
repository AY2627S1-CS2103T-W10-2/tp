package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class MessagesTest {

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
}
