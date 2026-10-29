package seedu.address.logic.parser;

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

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Context;

/**
 * Parses the {@code c/CONTEXT} form of the archive and unarchive commands.
 */
class ContextArgumentParser {

    /**
     * Every prefix defined in {@link CliSyntax}. Tokenizing with all of them keeps a prefix of another command, such
     * as {@code +c/} or {@code n/}, from being read as part of a context. A prefix added to {@code CliSyntax} must
     * be added here too.
     */
    private static final Prefix[] ALL_PREFIXES = {
        PREFIX_NAME, PREFIX_PHONE, PREFIX_PHONE_REMOVE, PREFIX_EMAIL, PREFIX_EMAIL_REMOVE,
        PREFIX_CONTEXT, PREFIX_CONTEXT_ADD, PREFIX_CONTEXT_REMOVE, PREFIX_ALIAS, PREFIX_ALIAS_REMOVE,
        PREFIX_INTRODUCER, PREFIX_INTRODUCER_REMOVE, PREFIX_NOTE, PREFIX_NOTE_REMOVE
    };

    private ContextArgumentParser() {}

    /**
     * Returns the context in {@code args}, or an empty {@code Optional} if {@code args} has no c/ prefix, which
     * means the caller should parse {@code args} as an index instead.
     *
     * @param usage the usage message of the calling command, shown when the format is wrong.
     * @throws ParseException if {@code args} has c/ together with an index or another prefix, has more than one c/,
     *                        or has a context that is empty or invalid.
     */
    static Optional<Context> parse(String args, String usage) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, ALL_PREFIXES);
        List<String> contexts = argMultimap.getAllValues(PREFIX_CONTEXT);
        if (contexts.isEmpty()) {
            return Optional.empty();
        }

        boolean hasOtherPrefix = Stream.of(ALL_PREFIXES)
                .filter(prefix -> !prefix.equals(PREFIX_CONTEXT))
                .anyMatch(prefix -> !argMultimap.getAllValues(prefix).isEmpty());
        if (contexts.size() > 1 || hasOtherPrefix || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, usage));
        }

        return Optional.of(ParserUtil.parseContext(contexts.get(0)));
    }
}
