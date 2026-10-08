package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonMappingException;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.person.Context;

public class JsonAdaptedContextTest {
    private static final String VALID_DESCRIPTION = "CS2103T";

    @Test
    public void toModelType_validDescription_returnsContext() throws Exception {
        JsonAdaptedContext context = new JsonAdaptedContext(VALID_DESCRIPTION);
        assertEquals(new Context(VALID_DESCRIPTION), context.toModelType());
    }

    @Test
    public void toModelType_descriptionWithSurroundingSpaces_returnsTrimmedContext() throws Exception {
        JsonAdaptedContext context = new JsonAdaptedContext("  Hiking club ");
        assertEquals(new Context("Hiking club"), context.toModelType());
    }

    @Test
    public void toModelType_blankDescription_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class, Context.MESSAGE_CONSTRAINTS,
                new JsonAdaptedContext("")::toModelType);
        assertThrows(IllegalValueException.class, Context.MESSAGE_CONSTRAINTS,
                new JsonAdaptedContext("   ")::toModelType);
    }

    @Test
    public void toModelType_nullDescription_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class, Context.MESSAGE_CONSTRAINTS,
                new JsonAdaptedContext((String) null)::toModelType);
    }

    @Test
    public void constructor_fromContext_copiesDescription() {
        JsonAdaptedContext context = new JsonAdaptedContext(new Context(VALID_DESCRIPTION));
        assertEquals(VALID_DESCRIPTION, context.getDescription());
    }

    @Test
    public void toModelType_fromContext_roundTrips() throws Exception {
        Context original = new Context("Hiking club");
        assertEquals(original, new JsonAdaptedContext(original).toModelType());
    }

    @Test
    public void serialize_writesBareString() throws Exception {
        String json = JsonUtil.toJsonString(new JsonAdaptedContext(new Context(VALID_DESCRIPTION)));
        assertEquals("\"" + VALID_DESCRIPTION + "\"", json);
    }

    @Test
    public void deserialize_bareString_readsContext() throws Exception {
        JsonAdaptedContext context = JsonUtil.fromJsonString("\"" + VALID_DESCRIPTION + "\"",
                JsonAdaptedContext.class);
        assertEquals(new Context(VALID_DESCRIPTION), context.toModelType());
    }

    @Test
    public void deserialize_jsonObject_throwsException() {
        // contexts are stored as bare strings, so an object form is not accepted
        org.junit.jupiter.api.Assertions.assertThrows(JsonMappingException.class, (
            ) -> JsonUtil.fromJsonString("{\"description\":\"" + VALID_DESCRIPTION + "\"}",
                    JsonAdaptedContext.class));
    }

    @Test
    public void saveAndLoad_roundTrip_preservesContext() throws IOException, IllegalValueException {
        Context original = new Context("CS2103T");
        String json = JsonUtil.toJsonString(new JsonAdaptedContext(original));
        Context loaded = JsonUtil.fromJsonString(json, JsonAdaptedContext.class).toModelType();
        assertEquals(original, loaded);
    }

}
