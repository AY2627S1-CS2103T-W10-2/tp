package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonMappingException;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.person.Context;

public class JsonAdaptedContextTest {
    private static final String VALID_DESCRIPTION = "CS2103T";

    @Test
    public void toModelType_validDescription_returnsContext() throws Exception {
        assertEquals(new Context(VALID_DESCRIPTION), new JsonAdaptedContext(VALID_DESCRIPTION).toModelType());
    }

    @Test
    public void toModelType_invalidDescription_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class, Context.MESSAGE_CONSTRAINTS,
                new JsonAdaptedContext("   ")::toModelType);
        assertThrows(IllegalValueException.class, Context.MESSAGE_CONSTRAINTS,
                new JsonAdaptedContext("(orbital)")::toModelType);
    }

    @Test
    public void toModelType_nullDescription_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class, Context.MESSAGE_CONSTRAINTS,
                new JsonAdaptedContext((String) null)::toModelType);
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
        // contexts are stored as bare strings, so the object form must not be accepted
        assertThrows(JsonMappingException.class, () -> JsonUtil.fromJsonString(
                "{\"description\":\"" + VALID_DESCRIPTION + "\"}", JsonAdaptedContext.class));
    }

}
