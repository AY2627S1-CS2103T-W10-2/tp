package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.storage.JsonAdaptedContact.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Contact;
import seedu.address.model.person.Context;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;

public class JsonAdaptedContactTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+651234";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_CONTEXT = " ";

    private static final String VALID_NAME = "Benson Meier";
    private static final String VALID_PHONE = "98765432";
    private static final String VALID_EMAIL = "johnd@example.com";
    private static final List<JsonAdaptedContext> VALID_CONTEXTS =
            List.of(new JsonAdaptedContext("CS2103T"), new JsonAdaptedContext("Hiking club"));

    private static final Contact BENSON = new Contact(new Name(VALID_NAME), new Phone(VALID_PHONE),
            new Email(VALID_EMAIL), Set.of(new Context("CS2103T"), new Context("Hiking club")));

    @Test
    public void toModelType_validContactDetails_returnsContact() throws Exception {
        JsonAdaptedContact contact = new JsonAdaptedContact(BENSON);
        assertEquals(BENSON, contact.toModelType());
    }

    @Test
    public void toModelType_contactWithoutPhoneAndEmail_returnsContact() throws Exception {
        Contact withoutOptionals = new Contact(new Name(VALID_NAME), Set.of(new Context("CS2103T")));
        Contact converted = new JsonAdaptedContact(withoutOptionals).toModelType();
        assertEquals(withoutOptionals, converted);
        assertTrue(converted.getPhone().isEmpty());
        assertTrue(converted.getEmail().isEmpty());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedContact contact =
                new JsonAdaptedContact(INVALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_CONTEXTS);
        assertThrows(IllegalValueException.class, Name.MESSAGE_CONSTRAINTS, contact::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedContact contact = new JsonAdaptedContact(null, VALID_PHONE, VALID_EMAIL, VALID_CONTEXTS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, contact::toModelType);
    }

    @Test
    public void toModelType_invalidPhone_throwsIllegalValueException() {
        JsonAdaptedContact contact =
                new JsonAdaptedContact(VALID_NAME, INVALID_PHONE, VALID_EMAIL, VALID_CONTEXTS);
        assertThrows(IllegalValueException.class, Phone.MESSAGE_CONSTRAINTS, contact::toModelType);
    }

    @Test
    public void toModelType_nullPhone_returnsContactWithoutPhone() throws Exception {
        JsonAdaptedContact contact = new JsonAdaptedContact(VALID_NAME, null, VALID_EMAIL, VALID_CONTEXTS);
        assertTrue(contact.toModelType().getPhone().isEmpty());
    }

    @Test
    public void toModelType_invalidEmail_throwsIllegalValueException() {
        JsonAdaptedContact contact =
                new JsonAdaptedContact(VALID_NAME, VALID_PHONE, INVALID_EMAIL, VALID_CONTEXTS);
        assertThrows(IllegalValueException.class, Email.MESSAGE_CONSTRAINTS, contact::toModelType);
    }

    @Test
    public void toModelType_nullEmail_returnsContactWithoutEmail() throws Exception {
        JsonAdaptedContact contact = new JsonAdaptedContact(VALID_NAME, VALID_PHONE, null, VALID_CONTEXTS);
        assertTrue(contact.toModelType().getEmail().isEmpty());
    }

    @Test
    public void toModelType_invalidContext_throwsIllegalValueException() {
        List<JsonAdaptedContext> invalidContexts = new ArrayList<>(VALID_CONTEXTS);
        invalidContexts.add(new JsonAdaptedContext(INVALID_CONTEXT));
        JsonAdaptedContact contact =
                new JsonAdaptedContact(VALID_NAME, VALID_PHONE, VALID_EMAIL, invalidContexts);
        assertThrows(IllegalValueException.class, Context.MESSAGE_CONSTRAINTS, contact::toModelType);
    }

    @Test
    public void toModelType_noContexts_throwsIllegalValueException() {
        JsonAdaptedContact contact =
                new JsonAdaptedContact(VALID_NAME, VALID_PHONE, VALID_EMAIL, new ArrayList<>());
        assertThrows(IllegalValueException.class, Contact.MESSAGE_CONTEXT_REQUIRED, contact::toModelType);
    }

    @Test
    public void toModelType_nullContexts_throwsIllegalValueException() {
        JsonAdaptedContact contact = new JsonAdaptedContact(VALID_NAME, VALID_PHONE, VALID_EMAIL, null);
        assertThrows(IllegalValueException.class, Contact.MESSAGE_CONTEXT_REQUIRED, contact::toModelType);
    }

}
