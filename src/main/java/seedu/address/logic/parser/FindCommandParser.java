package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTEXT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.PersonContainsKeywordsPredicate;
import seedu.address.model.person.PersonMatchesCluesPredicate;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    private static final Pattern PREFIX_TOKEN = Pattern.compile("(?<!\\S)([A-Za-z][A-Za-z0-9+-]*/)");

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw invalidFormat();
        }

        List<String> prefixTokens = getPrefixTokens(trimmedArgs);
        if (prefixTokens.isEmpty()) {
            return parseKeywordSearch(trimmedArgs);
        }

        if (prefixTokens.stream().anyMatch(prefix -> !prefix.equals(PREFIX_NAME.getPrefix())
                && !prefix.equals(PREFIX_CONTEXT.getPrefix()))) {
            throw invalidFormat();
        }

        return parseStructuredSearch(trimmedArgs);
    }

    private FindCommand parseKeywordSearch(String args) {
        String[] keywords = args.split("\\s+");
        return new FindCommand(new PersonContainsKeywordsPredicate(List.of(keywords)));
    }

    private FindCommand parseStructuredSearch(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(" " + args, PREFIX_NAME, PREFIX_CONTEXT);
        if (!argMultimap.getPreamble().isEmpty()) {
            throw invalidFormat();
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME);

        Optional<String> nameClue = argMultimap.getValue(PREFIX_NAME);
        List<String> contextClues = argMultimap.getAllValues(PREFIX_CONTEXT);
        if (nameClue.isPresent() && nameClue.get().isBlank()) {
            throw new ParseException(FindCommand.MESSAGE_EMPTY_NAME_CLUE);
        }
        if (contextClues.stream().anyMatch(String::isBlank)) {
            throw new ParseException(FindCommand.MESSAGE_EMPTY_CONTEXT_CLUE);
        }

        Optional<String> normalisedNameClue = nameClue.map(FindCommandParser::normaliseClue);
        List<String> normalisedContextClues = removeEquivalentClues(contextClues.stream()
                .map(FindCommandParser::normaliseClue)
                .toList());

        return new FindCommand(new PersonMatchesCluesPredicate(normalisedNameClue, normalisedContextClues));
    }

    private static List<String> getPrefixTokens(String args) {
        List<String> prefixTokens = new ArrayList<>();
        Matcher matcher = PREFIX_TOKEN.matcher(args);
        while (matcher.find()) {
            prefixTokens.add(matcher.group(1));
        }
        return prefixTokens;
    }

    private static List<String> removeEquivalentClues(List<String> clues) {
        List<String> distinctClues = new ArrayList<>();
        for (String clue : clues) {
            boolean isDuplicate = distinctClues.stream().anyMatch(existing -> existing.equalsIgnoreCase(clue));
            if (!isDuplicate) {
                distinctClues.add(clue);
            }
        }
        return distinctClues;
    }

    private static String normaliseClue(String clue) {
        return clue.trim().replaceAll("\\s+", " ");
    }

    private static ParseException invalidFormat() {
        return new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

}
