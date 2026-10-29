package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code AddCommand}.
 */
public class AddCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_newPerson_success() {
        Person validPerson = new PersonBuilder().build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(validPerson);

        assertCommandSuccess(new AddCommand(validPerson), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPerson)),
                expectedModel);
    }

    @Test
    public void execute_duplicatePerson_throwsCommandException() {
        // matches on both phone and email, so the phone is named
        Person personInList = model.getAddressBook().getPersonList().get(0);
        assertCommandFailure(new AddCommand(personInList), model,
                "This person already exists: Alice Pauline has the same phone number (94351253).");
    }

    @Test
    public void execute_duplicatePhone_throwsCommandException() {
        Person samePhone = new PersonBuilder().withPhone("94351253").build();
        assertCommandFailure(new AddCommand(samePhone), model,
                "This person already exists: Alice Pauline has the same phone number (94351253).");
    }

    @Test
    public void execute_duplicateEmailDifferentCase_throwsCommandException() {
        // the existing person's email is shown as stored
        Person sameEmail = new PersonBuilder().withEmail("ALICE@example.com").build();
        assertCommandFailure(new AddCommand(sameEmail), model,
                "This person already exists: Alice Pauline has the same email (alice@example.com).");
    }

    @Test
    public void execute_duplicateOfArchivedPerson_throwsCommandException() {
        // archived persons still count as duplicates, and the message says they are archived
        Person alice = model.getAddressBook().getPersonList().get(0);
        model.setPerson(alice, alice.copyWithArchived(true));
        Person samePhone = new PersonBuilder().withPhone("94351253").build();
        assertCommandFailure(new AddCommand(samePhone), model,
                "This person already exists: Alice Pauline has the same phone number (94351253)."
                        + " This person is archived; run archives to see them.");
    }

    @Test
    public void execute_clashesWithSeveralPersons_reportsFirstInList() {
        // phone matches Benson, email matches Alice; Alice comes first in the list
        Person clashesWithBoth = new PersonBuilder().withPhone("98765432").withEmail("alice@example.com").build();
        assertCommandFailure(new AddCommand(clashesWithBoth), model,
                "This person already exists: Alice Pauline has the same email (alice@example.com).");
    }

}
