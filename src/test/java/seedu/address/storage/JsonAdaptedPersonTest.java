package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Context;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.testutil.PersonBuilder;

public class JsonAdaptedPersonTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+65";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_CONTEXT = " ";

    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_PHONE = BENSON.getPhone().get().toString();
    private static final String VALID_EMAIL = BENSON.getEmail().get().toString();
    private static final List<JsonAdaptedContext> VALID_CONTEXTS = BENSON.getContexts().stream()
            .map(JsonAdaptedContext::new)
            .collect(Collectors.toList());

    @Test
    public void toModelType_validPersonDetails_returnsPerson() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(BENSON);
        assertEquals(BENSON, person.toModelType());
    }

    @Test
    public void toModelType_personWithoutPhoneAndEmail_returnsPerson() throws Exception {
        Person withoutOptionals = new PersonBuilder(BENSON).withoutPhone().withoutEmail().build();
        Person converted = new JsonAdaptedPerson(withoutOptionals).toModelType();
        assertEquals(withoutOptionals, converted);
        assertTrue(converted.getPhone().isEmpty() && converted.getEmail().isEmpty());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(INVALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_CONTEXTS);
        assertThrows(IllegalValueException.class, Name.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(null, VALID_PHONE, VALID_EMAIL, VALID_CONTEXTS);
        String expectedMessage = String.format(JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT,
                Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, INVALID_PHONE, VALID_EMAIL, VALID_CONTEXTS);
        assertThrows(IllegalValueException.class, Phone.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_nullPhone_returnsPersonWithoutPhone() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, null, VALID_EMAIL, VALID_CONTEXTS);
        assertTrue(person.toModelType().getPhone().isEmpty());
    }

    @Test
    public void toModelType_invalidEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, INVALID_EMAIL, VALID_CONTEXTS);
        assertThrows(IllegalValueException.class, Email.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_nullEmail_returnsPersonWithoutEmail() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, null, VALID_CONTEXTS);
        assertTrue(person.toModelType().getEmail().isEmpty());
    }

    @Test
    public void toModelType_invalidContext_throwsIllegalValueException() {
        List<JsonAdaptedContext> invalidContexts = new ArrayList<>(VALID_CONTEXTS);
        invalidContexts.add(new JsonAdaptedContext(INVALID_CONTEXT));
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, invalidContexts);
        assertThrows(IllegalValueException.class, Context.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_nullContextEntry_throwsIllegalValueException() {
        List<JsonAdaptedContext> contextsWithNull = new ArrayList<>(VALID_CONTEXTS);
        contextsWithNull.add(null);
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, contextsWithNull);
        String expectedMessage = String.format(JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT,
                Context.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_noContexts_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, new ArrayList<>());
        assertThrows(IllegalValueException.class, Person.MESSAGE_CONTEXT_REQUIRED, person::toModelType);
    }

}
