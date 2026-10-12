package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Context;

/**
 * Jackson-friendly version of {@link Context}.
 */
class JsonAdaptedContext {

    private final String description;

    /**
     * Constructs a {@code JsonAdaptedContext} with the given {@code description}.
     */
    @JsonCreator
    public JsonAdaptedContext(String description) {
        this.description = description;
    }

    /**
     * Converts a given {@code Context} into this class for Jackson use.
     */
    public JsonAdaptedContext(Context source) {
        description = source.description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

    /**
     * Converts this Jackson-friendly adapted context object into the model's {@code Context} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted context.
     */
    public Context toModelType() throws IllegalValueException {
        if (!Context.isValidDescription(description)) {
            throw new IllegalValueException(Context.MESSAGE_CONSTRAINTS);
        }
        return new Context(description);
    }

}
