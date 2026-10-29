package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ALIAS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTEXT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INTRODUCER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.PersonContainsKeywordsPredicate;
import seedu.address.model.person.PersonMatchesCluesPredicate;
import seedu.address.model.person.PersonSearchClues;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    private static final Prefix[] SEARCH_PREFIXES = {
        PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_CONTEXT,
        PREFIX_ALIAS, PREFIX_INTRODUCER, PREFIX_NOTE
    };

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

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(" " + trimmedArgs, SEARCH_PREFIXES);
        if (!hasSupportedPrefix(argMultimap)) {
            return parseKeywordSearch(trimmedArgs);
        }

        return parseStructuredSearch(argMultimap);
    }

    private FindCommand parseKeywordSearch(String args) {
        String[] keywords = args.split("\\s+");
        return new FindCommand(new PersonContainsKeywordsPredicate(List.of(keywords)));
    }

    private FindCommand parseStructuredSearch(ArgumentMultimap argMultimap) throws ParseException {
        if (!argMultimap.getPreamble().isEmpty()) {
            throw invalidFormat();
        }

        argMultimap.verifyNoDuplicatePrefixesFor(
                PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ALIAS, PREFIX_INTRODUCER, PREFIX_NOTE);

        PersonSearchClues searchClues = new PersonSearchClues();
        parseClue(argMultimap, PREFIX_NAME, FindCommand.MESSAGE_EMPTY_NAME_CLUE)
                .ifPresent(searchClues::withNameClue);
        parseClue(argMultimap, PREFIX_PHONE, FindCommand.MESSAGE_EMPTY_PHONE_CLUE)
                .ifPresent(searchClues::withPhoneClue);
        parseClue(argMultimap, PREFIX_EMAIL, FindCommand.MESSAGE_EMPTY_EMAIL_CLUE)
                .ifPresent(searchClues::withEmailClue);
        searchClues.withContextClues(parseContextClues(argMultimap));
        parseClue(argMultimap, PREFIX_ALIAS, FindCommand.MESSAGE_EMPTY_ALIAS_CLUE)
                .ifPresent(searchClues::withAliasClue);
        parseClue(argMultimap, PREFIX_INTRODUCER, FindCommand.MESSAGE_EMPTY_INTRODUCER_CLUE)
                .ifPresent(searchClues::withIntroducerClue);
        parseClue(argMultimap, PREFIX_NOTE, FindCommand.MESSAGE_EMPTY_NOTE_CLUE)
                .ifPresent(searchClues::withNoteClue);

        return new FindCommand(new PersonMatchesCluesPredicate(searchClues));
    }

    private static boolean hasSupportedPrefix(ArgumentMultimap argMultimap) {
        return Arrays.stream(SEARCH_PREFIXES)
                .anyMatch(prefix -> argMultimap.getValue(prefix).isPresent());
    }

    private static Optional<String> parseClue(ArgumentMultimap argMultimap, Prefix prefix,
            String emptyClueMessage) throws ParseException {
        Optional<String> clue = argMultimap.getValue(prefix);
        requireNonBlank(clue, emptyClueMessage);
        return clue.map(FindCommandParser::normaliseClue);
    }

    private static List<String> parseContextClues(ArgumentMultimap argMultimap) throws ParseException {
        List<String> contextClues = argMultimap.getAllValues(PREFIX_CONTEXT);
        if (contextClues.stream().anyMatch(String::isBlank)) {
            throw new ParseException(FindCommand.MESSAGE_EMPTY_CONTEXT_CLUE);
        }
        return removeEquivalentClues(contextClues.stream()
                .map(FindCommandParser::normaliseClue)
                .toList());
    }

    private static void requireNonBlank(Optional<String> clue, String message) throws ParseException {
        if (clue.isPresent() && clue.get().isBlank()) {
            throw new ParseException(message);
        }
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
