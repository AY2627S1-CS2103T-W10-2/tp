package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ALIAS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ALIAS_REMOVE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTEXT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTEXT_ADD;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTEXT_REMOVE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL_REMOVE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INTRODUCER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INTRODUCER_REMOVE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE_REMOVE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE_REMOVE;

import java.util.Optional;
import java.util.function.Consumer;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Alias;
import seedu.address.model.person.Email;
import seedu.address.model.person.Introducer;
import seedu.address.model.person.Note;
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
    private static final String MESSAGE_SET_AND_REMOVE =
            "Cannot set and remove the %1$s in the same command.";

    private static final OptionalField<Phone> PHONE_FIELD = new OptionalField<>(PREFIX_PHONE,
            PREFIX_PHONE_REMOVE, "phone number", "PHONE", ParserUtil.MESSAGE_EMPTY_PHONE,
            ParserUtil::parsePhone);
    private static final OptionalField<Email> EMAIL_FIELD = new OptionalField<>(PREFIX_EMAIL,
            PREFIX_EMAIL_REMOVE, "email", "EMAIL", ParserUtil.MESSAGE_EMPTY_EMAIL, ParserUtil::parseEmail);
    private static final OptionalField<Alias> ALIAS_FIELD = new OptionalField<>(PREFIX_ALIAS,
            PREFIX_ALIAS_REMOVE, "alias", "ALIAS", ParserUtil.MESSAGE_EMPTY_ALIAS, ParserUtil::parseAlias);
    private static final OptionalField<Introducer> INTRODUCER_FIELD = new OptionalField<>(PREFIX_INTRODUCER,
            PREFIX_INTRODUCER_REMOVE, "introducer", "INTRODUCER", ParserUtil.MESSAGE_EMPTY_INTRODUCER,
            ParserUtil::parseIntroducer);
    private static final OptionalField<Note> NOTE_FIELD = new OptionalField<>(PREFIX_NOTE, PREFIX_NOTE_REMOVE,
            "note", "NOTE", ParserUtil.MESSAGE_EMPTY_NOTE, ParserUtil::parseNote);

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenizeWithLastPrefix(args, PREFIX_NOTE,
                PREFIX_NAME, PREFIX_PHONE, PREFIX_PHONE_REMOVE, PREFIX_EMAIL, PREFIX_EMAIL_REMOVE,
                PREFIX_ALIAS, PREFIX_ALIAS_REMOVE, PREFIX_INTRODUCER, PREFIX_INTRODUCER_REMOVE,
                PREFIX_NOTE_REMOVE, PREFIX_CONTEXT, PREFIX_CONTEXT_ADD, PREFIX_CONTEXT_REMOVE);

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
                PREFIX_EMAIL, PREFIX_EMAIL_REMOVE, PREFIX_ALIAS, PREFIX_ALIAS_REMOVE,
                PREFIX_INTRODUCER, PREFIX_INTRODUCER_REMOVE, PREFIX_NOTE, PREFIX_NOTE_REMOVE);

        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            editPersonDescriptor.setName(ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get()));
        }
        parseOptionalField(argMultimap, PHONE_FIELD,
                editPersonDescriptor::setPhone, editPersonDescriptor::setPhoneRemoved);
        parseOptionalField(argMultimap, EMAIL_FIELD,
                editPersonDescriptor::setEmail, editPersonDescriptor::setEmailRemoved);
        parseOptionalField(argMultimap, ALIAS_FIELD,
                editPersonDescriptor::setAlias, editPersonDescriptor::setAliasRemoved);
        parseOptionalField(argMultimap, INTRODUCER_FIELD,
                editPersonDescriptor::setIntroducer, editPersonDescriptor::setIntroducerRemoved);
        parseOptionalField(argMultimap, NOTE_FIELD,
                editPersonDescriptor::setNote, editPersonDescriptor::setNoteRemoved);
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
     * @throws ParseException if the remove prefix has a value, is given with the set prefix,
     *     or the set prefix is empty or has an invalid value.
     */
    private static <T> void parseOptionalField(ArgumentMultimap argMultimap, OptionalField<T> field,
            Consumer<T> setValue, RemovalSetter setRemoved) throws ParseException {
        // Each is empty if its prefix was not typed, e.g. value holds "91234567" for p/91234567
        Optional<String> value = argMultimap.getValue(field.setPrefix());
        Optional<String> valueToRemove = argMultimap.getValue(field.removePrefix());

        // The checks run in this order so a command breaking several rules gets the most relevant error
        // The remove prefix takes no value, e.g. -p/123
        if (valueToRemove.isPresent() && !valueToRemove.get().isBlank()) {
            throw new ParseException(String.format(MESSAGE_REMOVE_WITH_VALUE,
                    field.removePrefix(), field.setPrefix(), field.placeholder(), field.description()));
        }
        // The field cannot be set and removed together, e.g. p/91234567 -p/
        if (value.isPresent() && valueToRemove.isPresent()) {
            throw new ParseException(String.format(MESSAGE_SET_AND_REMOVE, field.description()));
        }
        // An empty set prefix is likely a slip, so the error points to the remove prefix, e.g. p/
        if (value.isPresent() && value.get().isBlank()) {
            throw new ParseException(String.format(MESSAGE_EMPTY_VALUE,
                    field.emptyMessage(), field.removePrefix(), field.description()));
        }

        // Record the parsed new value and the remove flag in the descriptor
        if (value.isPresent()) {
            setValue.accept(field.parser().parse(value.get()));
        }
        setRemoved.set(valueToRemove.isPresent());
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

    /**
     * Represents a function that records whether a field should be removed, such as
     * {@link EditCommand.EditPersonDescriptor#setPhoneRemoved}.
     */
    @FunctionalInterface
    private interface RemovalSetter {
        void set(boolean isRemoved);
    }

}
