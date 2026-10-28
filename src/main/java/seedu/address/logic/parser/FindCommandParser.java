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
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.PersonContainsKeywordsPredicate;
import seedu.address.model.person.PersonMatchesCluesPredicate;
import seedu.address.model.person.PersonSearchClues;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

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

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(" " + trimmedArgs,
                PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_CONTEXT,
                PREFIX_ALIAS, PREFIX_INTRODUCER, PREFIX_NOTE);
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

        Optional<String> nameClue = argMultimap.getValue(PREFIX_NAME);
        Optional<String> phoneClue = argMultimap.getValue(PREFIX_PHONE);
        Optional<String> emailClue = argMultimap.getValue(PREFIX_EMAIL);
        List<String> contextClues = argMultimap.getAllValues(PREFIX_CONTEXT);
        Optional<String> aliasClue = argMultimap.getValue(PREFIX_ALIAS);
        Optional<String> introducerClue = argMultimap.getValue(PREFIX_INTRODUCER);
        Optional<String> noteClue = argMultimap.getValue(PREFIX_NOTE);

        requireNonBlank(nameClue, FindCommand.MESSAGE_EMPTY_NAME_CLUE);
        requireNonBlank(phoneClue, FindCommand.MESSAGE_EMPTY_PHONE_CLUE);
        requireNonBlank(emailClue, FindCommand.MESSAGE_EMPTY_EMAIL_CLUE);
        if (contextClues.stream().anyMatch(String::isBlank)) {
            throw new ParseException(FindCommand.MESSAGE_EMPTY_CONTEXT_CLUE);
        }
        requireNonBlank(aliasClue, FindCommand.MESSAGE_EMPTY_ALIAS_CLUE);
        requireNonBlank(introducerClue, FindCommand.MESSAGE_EMPTY_INTRODUCER_CLUE);
        requireNonBlank(noteClue, FindCommand.MESSAGE_EMPTY_NOTE_CLUE);

        Optional<String> normalisedNameClue = nameClue.map(FindCommandParser::normaliseClue);
        Optional<String> normalisedPhoneClue = phoneClue.map(FindCommandParser::normaliseClue);
        Optional<String> normalisedEmailClue = emailClue.map(FindCommandParser::normaliseClue);
        List<String> normalisedContextClues = removeEquivalentClues(contextClues.stream()
                .map(FindCommandParser::normaliseClue)
                .toList());
        Optional<String> normalisedAliasClue = aliasClue.map(FindCommandParser::normaliseClue);
        Optional<String> normalisedIntroducerClue = introducerClue.map(FindCommandParser::normaliseClue);
        Optional<String> normalisedNoteClue = noteClue.map(FindCommandParser::normaliseClue);

        PersonSearchClues searchClues = new PersonSearchClues()
                .withContextClues(normalisedContextClues);
        normalisedNameClue.ifPresent(searchClues::withNameClue);
        normalisedPhoneClue.ifPresent(searchClues::withPhoneClue);
        normalisedEmailClue.ifPresent(searchClues::withEmailClue);
        normalisedAliasClue.ifPresent(searchClues::withAliasClue);
        normalisedIntroducerClue.ifPresent(searchClues::withIntroducerClue);
        normalisedNoteClue.ifPresent(searchClues::withNoteClue);

        return new FindCommand(new PersonMatchesCluesPredicate(searchClues));
    }

    private static boolean hasSupportedPrefix(ArgumentMultimap argMultimap) {
        return Stream.of(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_CONTEXT,
                        PREFIX_ALIAS, PREFIX_INTRODUCER, PREFIX_NOTE)
                .anyMatch(prefix -> argMultimap.getValue(prefix).isPresent());
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
