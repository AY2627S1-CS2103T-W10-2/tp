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

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new EditCommand object
 */
public class EditCommandParser implements Parser<EditCommand> {

    public static final String MESSAGE_EMPTY_PHONE = ParserUtil.MESSAGE_EMPTY_PHONE
            + " Use " + PREFIX_PHONE_REMOVE + " to remove the phone number.";
    public static final String MESSAGE_EMPTY_EMAIL = ParserUtil.MESSAGE_EMPTY_EMAIL
            + " Use " + PREFIX_EMAIL_REMOVE + " to remove the email.";
    public static final String MESSAGE_REMOVE_PHONE_WITH_VALUE = PREFIX_PHONE_REMOVE
            + " does not take a value. Use " + PREFIX_PHONE + "PHONE to change the phone number.";
    public static final String MESSAGE_REMOVE_EMAIL_WITH_VALUE = PREFIX_EMAIL_REMOVE
            + " does not take a value. Use " + PREFIX_EMAIL + "EMAIL to change the email.";
    public static final String MESSAGE_SET_AND_REMOVE_PHONE =
            "Cannot set and remove the phone number in the same command.";
    public static final String MESSAGE_SET_AND_REMOVE_EMAIL =
            "Cannot set and remove the email in the same command.";
    public static final String MESSAGE_INVALID_CONTEXT_PREFIX = "Use +c/ to add a context or -c/ to remove one.";

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
        parsePhoneForEdit(argMultimap, editPersonDescriptor);
        parseEmailForEdit(argMultimap, editPersonDescriptor);
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
     * Records the phone to set, or that the phone should be removed, in {@code descriptor}.
     * Mirrors {@link #parseEmailForEdit}, so a change to one should be made to the other.
     *
     * @throws ParseException if {@code -p/} has a value, is given with {@code p/}, or {@code p/} is empty.
     */
    private static void parsePhoneForEdit(ArgumentMultimap argMultimap, EditPersonDescriptor descriptor)
            throws ParseException {
        Optional<String> phone = argMultimap.getValue(PREFIX_PHONE);
        Optional<String> phoneToRemove = argMultimap.getValue(PREFIX_PHONE_REMOVE);

        if (phoneToRemove.isPresent() && !phoneToRemove.get().isBlank()) {
            throw new ParseException(MESSAGE_REMOVE_PHONE_WITH_VALUE);
        }
        if (phone.isPresent() && phoneToRemove.isPresent()) {
            throw new ParseException(MESSAGE_SET_AND_REMOVE_PHONE);
        }
        if (phone.isPresent() && phone.get().isBlank()) {
            throw new ParseException(MESSAGE_EMPTY_PHONE);
        }

        if (phone.isPresent()) {
            descriptor.setPhone(ParserUtil.parsePhone(phone.get()));
        }
        descriptor.setPhoneRemoved(phoneToRemove.isPresent());
    }

    /**
     * Records the email to set, or that the email should be removed, in {@code descriptor}.
     * Mirrors {@link #parsePhoneForEdit}, so a change to one should be made to the other.
     *
     * @throws ParseException if {@code -e/} has a value, is given with {@code e/}, or {@code e/} is empty.
     */
    private static void parseEmailForEdit(ArgumentMultimap argMultimap, EditPersonDescriptor descriptor)
            throws ParseException {
        Optional<String> email = argMultimap.getValue(PREFIX_EMAIL);
        Optional<String> emailToRemove = argMultimap.getValue(PREFIX_EMAIL_REMOVE);

        if (emailToRemove.isPresent() && !emailToRemove.get().isBlank()) {
            throw new ParseException(MESSAGE_REMOVE_EMAIL_WITH_VALUE);
        }
        if (email.isPresent() && emailToRemove.isPresent()) {
            throw new ParseException(MESSAGE_SET_AND_REMOVE_EMAIL);
        }
        if (email.isPresent() && email.get().isBlank()) {
            throw new ParseException(MESSAGE_EMPTY_EMAIL);
        }

        if (email.isPresent()) {
            descriptor.setEmail(ParserUtil.parseEmail(email.get()));
        }
        descriptor.setEmailRemoved(emailToRemove.isPresent());
    }

}
