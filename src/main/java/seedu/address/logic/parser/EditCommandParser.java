package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTEXT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTEXT_ADD;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTEXT_REMOVE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL_REMOVE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE_REMOVE;

import java.util.Optional;
import java.util.function.Consumer;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Email;
import seedu.address.model.person.Phone;

/**
 * Parses input arguments and creates a new EditCommand object
 */
public class EditCommandParser implements Parser<EditCommand> {

    public static final String MESSAGE_INVALID_CONTEXT_PREFIX = "Use +c/ to add a context or -c/ to remove one.";

    // filled in with the details of an OptionalField
    private static final String MESSAGE_EMPTY_VALUE = "%1$s Use %2$s to remove the %3$s.";
    private static final String MESSAGE_REMOVE_WITH_VALUE =
            "%1$s does not take a value. Use %2$s%3$s to change the %4$s.";
    private static final String MESSAGE_SET_AND_REMOVE = "Cannot set and remove the %1$s in the same command.";

    private static final OptionalField<Phone> PHONE_FIELD = new OptionalField<>(PREFIX_PHONE, PREFIX_PHONE_REMOVE,
            "phone number", "PHONE", ParserUtil.MESSAGE_EMPTY_PHONE, ParserUtil::parsePhone);
    private static final OptionalField<Email> EMAIL_FIELD = new OptionalField<>(PREFIX_EMAIL, PREFIX_EMAIL_REMOVE,
            "email", "EMAIL", ParserUtil.MESSAGE_EMPTY_EMAIL, ParserUtil::parseEmail);

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE,
                PREFIX_PHONE_REMOVE, PREFIX_EMAIL, PREFIX_EMAIL_REMOVE,
                PREFIX_CONTEXT, PREFIX_CONTEXT_ADD, PREFIX_CONTEXT_REMOVE);

        Index index;

        if (argMultimap.getValue(PREFIX_CONTEXT).isPresent()) {
            throw new ParseException(MESSAGE_INVALID_CONTEXT_PREFIX);
        }

        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE), pe);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_PHONE_REMOVE,
                PREFIX_EMAIL, PREFIX_EMAIL_REMOVE);

        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            editPersonDescriptor.setName(ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get()));
        }
        parseOptionalField(argMultimap, PHONE_FIELD,
                editPersonDescriptor::setPhone, editPersonDescriptor::setPhoneRemoved);
        parseOptionalField(argMultimap, EMAIL_FIELD,
                editPersonDescriptor::setEmail, editPersonDescriptor::setEmailRemoved);
        if (!argMultimap.getAllValues(PREFIX_CONTEXT_ADD).isEmpty()) {
            editPersonDescriptor.setContextsToAdd(
                    ParserUtil.parseContexts(argMultimap.getAllValues(PREFIX_CONTEXT_ADD)));
        }
        if (!argMultimap.getAllValues(PREFIX_CONTEXT_REMOVE).isEmpty()) {
            editPersonDescriptor.setContextsToRemove(
                    ParserUtil.parseContexts(argMultimap.getAllValues(PREFIX_CONTEXT_REMOVE)));
        }

        if (!editPersonDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED + "\n" + EditCommand.MESSAGE_USAGE);
        }

        return new EditCommand(index, editPersonDescriptor);
    }

    /**
     * Records the value of {@code field} to set with {@code setValue}, and whether {@code field} should be
     * removed with {@code setRemoved}. Every field that edit can remove follows these rules.
     *
     * @throws ParseException if the remove prefix has a value, is given with the set prefix, or the set prefix
     *     is empty or has an invalid value.
     */
    private static <T> void parseOptionalField(ArgumentMultimap argMultimap, OptionalField<T> field,
            Consumer<T> setValue, Consumer<Boolean> setRemoved) throws ParseException {
        Optional<String> value = argMultimap.getValue(field.setPrefix());
        Optional<String> valueToRemove = argMultimap.getValue(field.removePrefix());

        if (valueToRemove.isPresent() && !valueToRemove.get().isBlank()) {
            throw new ParseException(String.format(MESSAGE_REMOVE_WITH_VALUE,
                    field.removePrefix(), field.setPrefix(), field.placeholder(), field.description()));
        }
        if (value.isPresent() && valueToRemove.isPresent()) {
            throw new ParseException(String.format(MESSAGE_SET_AND_REMOVE, field.description()));
        }
        if (value.isPresent() && value.get().isBlank()) {
            throw new ParseException(String.format(MESSAGE_EMPTY_VALUE,
                    field.emptyMessage(), field.removePrefix(), field.description()));
        }

        if (value.isPresent()) {
            setValue.accept(field.parser().parse(value.get()));
        }
        setRemoved.accept(valueToRemove.isPresent());
    }

    /**
     * An optional field that edit sets with {@code setPrefix} and removes with {@code removePrefix}.
     * {@code description} and {@code placeholder} name the field in error messages, and {@code emptyMessage}
     * is shown when {@code setPrefix} has no value.
     */
    private record OptionalField<T>(Prefix setPrefix, Prefix removePrefix, String description,
            String placeholder, String emptyMessage, FieldParser<T> parser) {}

    /**
     * Represents a function that parses the value of a field, such as {@link ParserUtil#parsePhone}.
     */
    @FunctionalInterface
    private interface FieldParser<T> {
        T parse(String value) throws ParseException;
    }

}
