package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.person.Alias;
import seedu.address.model.person.Context;
import seedu.address.model.person.Email;
import seedu.address.model.person.Introducer;
import seedu.address.model.person.Name;
import seedu.address.model.person.Note;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.testutil.PersonBuilder;

public class JsonAdaptedPersonTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonAdaptedPersonTest");
    private static final Path LEGACY_PERSON_FILE =
            TEST_DATA_FOLDER.resolve("legacyPersonWithoutAdditionalDetails.json");

    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+65";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_CONTEXT = " ";
    private static final String INVALID_ALIAS = "Alex123";
    private static final String INVALID_INTRODUCER = "Ben123";
    private static final String INVALID_NOTE = " ";

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
    public void toModelType_personWithAdditionalDetails_returnsPerson() throws Exception {
        Person withDetails = new PersonBuilder(BENSON).withAlias("Ben")
                .withIntroducer("Alex Tan").withNote("Met at Hack&Roll").build();
        assertEquals(withDetails, new JsonAdaptedPerson(withDetails).toModelType());
    }

    @Test
    public void toModelType_legacyJsonWithoutAdditionalDetails_returnsPerson() throws Exception {
        JsonAdaptedPerson readBack = JsonUtil.readJsonFile(LEGACY_PERSON_FILE, JsonAdaptedPerson.class).get();
        assertEquals(BENSON, readBack.toModelType());
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

    @Test
    public void toModelType_invalidAlias_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_CONTEXTS,
                INVALID_ALIAS, null, null);
        assertThrows(IllegalValueException.class, Alias.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_invalidIntroducer_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_CONTEXTS,
                null, INVALID_INTRODUCER, null);
        assertThrows(IllegalValueException.class, Introducer.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_invalidNote_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_CONTEXTS,
                null, null, INVALID_NOTE);
        assertThrows(IllegalValueException.class, Note.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_archivedPersonWithAdditionalDetails_returnsPerson() throws Exception {
        Person archivedWithDetails = new PersonBuilder(BENSON).withAlias("Ben")
                .withIntroducer("Alex Tan").withNote("Met at Hack&Roll").withArchived(true).build();
        assertEquals(archivedWithDetails, new JsonAdaptedPerson(archivedWithDetails).toModelType());
    }

    @Test
    public void toModelType_archivedPersonRoundTrips() throws Exception {
        Person archived = BENSON.copyWithArchived(true);
        assertEquals(archived, new JsonAdaptedPerson(archived).toModelType());
    }

    @Test
    public void toModelType_archivedNotABoolean_throwsIllegalValueException() {
        for (Object bad : new Object[] {"yes", "true", 5, 1, ""}) {
            JsonAdaptedPerson person =
                    new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_CONTEXTS, bad);
            assertThrows(IllegalValueException.class, JsonAdaptedPerson.MESSAGE_INVALID_ARCHIVED,
                    person::toModelType);
        }
    }

    @Test
    public void readFromJson_archivedKey_followsTheRules() throws Exception {
        String base = "{\"name\":\"Amy Bee\",\"contexts\":[\"CS2103T\"]";
        assertFalse(JsonUtil.fromJsonString(base + "}", JsonAdaptedPerson.class).toModelType().isArchived());
        assertFalse(JsonUtil.fromJsonString(base + ",\"archived\":null}", JsonAdaptedPerson.class)
                .toModelType().isArchived());
        assertFalse(JsonUtil.fromJsonString(base + ",\"archived\":false}", JsonAdaptedPerson.class)
                .toModelType().isArchived());
        assertTrue(JsonUtil.fromJsonString(base + ",\"archived\":true}", JsonAdaptedPerson.class)
                .toModelType().isArchived());
        // numbers and text are not booleans, even though they could be converted
        JsonAdaptedPerson five = JsonUtil.fromJsonString(base + ",\"archived\":5}", JsonAdaptedPerson.class);
        assertThrows(IllegalValueException.class, JsonAdaptedPerson.MESSAGE_INVALID_ARCHIVED, five::toModelType);
    }
}
