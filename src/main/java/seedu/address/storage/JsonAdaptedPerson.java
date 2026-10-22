package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Context;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";
    public static final String MESSAGE_INVALID_ARCHIVED = "Person's archived field must be true or false.";

    private final String name;
    private final String phone; // null if the person has no phone
    private final String email; // null if the person has no email
    private final List<JsonAdaptedContext> contexts = new ArrayList<>();
    // the raw JSON value: null if the key is missing (the person is not archived), otherwise it must be a Boolean
    private final Object archived;

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     * {@code archived} may be null, which means the person is not archived; any other value must be a Boolean.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name, @JsonProperty("phone") String phone,
            @JsonProperty("email") String email, @JsonProperty("contexts") List<JsonAdaptedContext> contexts,
            @JsonProperty("archived") Object archived) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        if (contexts != null) {
            this.contexts.addAll(contexts);
        }
        this.archived = archived;
    }

    /**
     * Constructs a {@code JsonAdaptedPerson} that is not archived.
     */
    public JsonAdaptedPerson(String name, String phone, String email, List<JsonAdaptedContext> contexts) {
        this(name, phone, email, contexts, null);
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;
        phone = source.getPhone().map(p -> p.value).orElse(null);
        email = source.getEmail().map(e -> e.value).orElse(null);
        contexts.addAll(source.getContexts().stream()
                .map(JsonAdaptedContext::new)
                .collect(Collectors.toList()));
        archived = source.isArchived();
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        final List<Context> personContexts = new ArrayList<>();
        for (JsonAdaptedContext context : contexts) {
            if (context == null) {
                throw new IllegalValueException(
                        String.format(MISSING_FIELD_MESSAGE_FORMAT, Context.class.getSimpleName()));
            }
            personContexts.add(context.toModelType());
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        // phone is optional
        Phone modelPhone = null;
        if (phone != null) {
            if (!Phone.isValidPhone(phone)) {
                throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
            }
            modelPhone = new Phone(phone);
        }

        // email is optional
        Email modelEmail = null;
        if (email != null) {
            if (!Email.isValidEmail(email)) {
                throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
            }
            modelEmail = new Email(email);
        }

        if (personContexts.isEmpty()) {
            throw new IllegalValueException(Person.MESSAGE_CONTEXT_REQUIRED);
        }
        final Set<Context> modelContexts = new HashSet<>(personContexts);
        // a missing or null "archived" means the person is not archived; anything but true or false is invalid
        if (archived != null && !(archived instanceof Boolean)) {
            throw new IllegalValueException(MESSAGE_INVALID_ARCHIVED);
        }
        final boolean modelArchived = Boolean.TRUE.equals(archived);
        return new Person(modelName, modelPhone, modelEmail, modelContexts, null, null, null, modelArchived);
    }

}
