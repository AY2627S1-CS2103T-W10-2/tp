package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTEXT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Context;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    public static final String MESSAGE_EMPTY_PHONE =
            ParserUtil.MESSAGE_EMPTY_PHONE + " Leave out " + PREFIX_PHONE + " if there is no phone number.";
    public static final String MESSAGE_EMPTY_EMAIL =
            ParserUtil.MESSAGE_EMPTY_EMAIL + " Leave out " + PREFIX_EMAIL + " if there is no email.";

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_CONTEXT);

        // name and at least one context are compulsory; phone and email are optional
        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_CONTEXT)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL);
        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        requireNonBlankIfPresent(argMultimap, PREFIX_PHONE, MESSAGE_EMPTY_PHONE);
        Phone phone = argMultimap.getValue(PREFIX_PHONE).isPresent()
                ? ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get()) : null;
        requireNonBlankIfPresent(argMultimap, PREFIX_EMAIL, MESSAGE_EMPTY_EMAIL);
        Email email = argMultimap.getValue(PREFIX_EMAIL).isPresent()
                ? ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get()) : null;
        Set<Context> contextList = ParserUtil.parseContexts(argMultimap.getAllValues(PREFIX_CONTEXT));

        Person person = new Person(name, phone, email, contextList);

        return new AddCommand(person);
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

    /**
     * Throws a {@code ParseException} with {@code message} if {@code prefix} is present with a blank value.
     * Lets add tell users that the field is optional, a hint that would mislead users of edit.
     */
    private static void requireNonBlankIfPresent(ArgumentMultimap argumentMultimap, Prefix prefix, String message)
            throws ParseException {
        Optional<String> value = argumentMultimap.getValue(prefix);
        if (value.isPresent() && value.get().isBlank()) {
            throw new ParseException(message);
        }
    }

}
