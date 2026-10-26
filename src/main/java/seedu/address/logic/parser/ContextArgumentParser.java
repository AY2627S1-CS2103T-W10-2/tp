package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTEXT;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Context;

/**
 * Parses the {@code c/CONTEXT} form of the archive and unarchive commands.
 */
class ContextArgumentParser {

    /** Matches any prefix such as n/ or c/. A context cannot contain a slash, so it is never mistaken for one. */
    private static final Pattern PREFIX_TOKEN = Pattern.compile("(?<!\\S)([A-Za-z][A-Za-z0-9+-]*/)");

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
        List<String> prefixTokens = getPrefixTokens(args);
        if (prefixTokens.isEmpty()) {
            return Optional.empty();
        }

        boolean onlyOneContextPrefix = prefixTokens.size() == 1
                && prefixTokens.get(0).equals(PREFIX_CONTEXT.getPrefix());
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(" " + args, PREFIX_CONTEXT);
        if (!onlyOneContextPrefix || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, usage));
        }

        return Optional.of(ParserUtil.parseContext(argMultimap.getValue(PREFIX_CONTEXT).orElse("")));
    }

    private static List<String> getPrefixTokens(String args) {
        List<String> prefixTokens = new ArrayList<>();
        Matcher matcher = PREFIX_TOKEN.matcher(args);
        while (matcher.find()) {
            prefixTokens.add(matcher.group(1));
        }
        return prefixTokens;
    }
}
