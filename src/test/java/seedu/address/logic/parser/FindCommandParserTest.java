package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.PersonContainsKeywordsPredicate;
import seedu.address.model.person.PersonMatchesCluesPredicate;

public class FindCommandParserTest {

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        // no leading and trailing whitespaces
        FindCommand expectedFindCommand =
                new FindCommand(new PersonContainsKeywordsPredicate(List.of("Alice", "Bob")));
        assertParseSuccess(parser, "Alice Bob", expectedFindCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n Alice \n \t Bob  \t", expectedFindCommand);
    }

    @Test
    public void parse_structuredArgs_returnsFindCommand() {
        FindCommand expectedFindCommand = new FindCommand(new PersonMatchesCluesPredicate(
                Optional.of("Tony Zhang"), List.of("CS2103T", "Hack")));

        assertParseSuccess(parser, "n/Tony   Zhang c/CS2103T c/Hack", expectedFindCommand);
        assertParseSuccess(parser, "c/CS2103T n/Tony Zhang c/Hack", expectedFindCommand);
    }

    @Test
    public void parse_nameOnly_returnsFindCommand() {
        FindCommand expectedFindCommand = new FindCommand(new PersonMatchesCluesPredicate(
                Optional.of("Tony"), List.of()));

        assertParseSuccess(parser, "n/Tony", expectedFindCommand);
    }

    @Test
    public void parse_contextOnly_returnsFindCommand() {
        FindCommand expectedFindCommand = new FindCommand(new PersonMatchesCluesPredicate(
                Optional.empty(), List.of("CS2103T")));

        assertParseSuccess(parser, "c/CS2103T", expectedFindCommand);
    }

    @Test
    public void parse_equivalentContextClues_collapsesDuplicates() {
        FindCommand expectedFindCommand = new FindCommand(new PersonMatchesCluesPredicate(
                Optional.empty(), List.of("CS2103T")));

        assertParseSuccess(parser, "c/CS2103T c/cs2103t", expectedFindCommand);
    }

    @Test
    public void parse_emptyStructuredClue_throwsParseException() {
        assertParseFailure(parser, "n/", FindCommand.MESSAGE_EMPTY_NAME_CLUE);
        assertParseFailure(parser, "c/", FindCommand.MESSAGE_EMPTY_CONTEXT_CLUE);
        assertParseFailure(parser, "n/Tony c/", FindCommand.MESSAGE_EMPTY_CONTEXT_CLUE);
    }

    @Test
    public void parse_duplicateNamePrefix_throwsParseException() {
        assertParseFailure(parser, "n/Tony n/Zhang",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
    }

    @Test
    public void parse_mixedSearchModes_throwsParseException() {
        assertParseFailure(parser, "Tony c/CS2103T",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_unsupportedOrUppercasePrefix_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "p/91234567", expectedMessage);
        assertParseFailure(parser, "x/value", expectedMessage);
        assertParseFailure(parser, "N/Tony", expectedMessage);
    }

}
