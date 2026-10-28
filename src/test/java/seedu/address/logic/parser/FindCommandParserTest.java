package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ALIAS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INTRODUCER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.PersonContainsKeywordsPredicate;
import seedu.address.model.person.PersonMatchesCluesPredicate;
import seedu.address.model.person.PersonSearchClues;

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
                new PersonSearchClues()
                        .withNameClue("Tony Zhang")
                        .withContextClues(List.of("CS2103T", "Hack"))));

        assertParseSuccess(parser, "n/Tony   Zhang c/CS2103T c/Hack", expectedFindCommand);
        assertParseSuccess(parser, "c/CS2103T n/Tony Zhang c/Hack", expectedFindCommand);
    }

    @Test
    public void parse_allStructuredFieldsInAnyOrder_returnsFindCommand() {
        PersonSearchClues searchClues = new PersonSearchClues()
                .withNameClue("Tony Zhang")
                .withPhoneClue("9123")
                .withEmailClue("@u.nus.edu")
                .withContextClues(List.of("CS2103T", "Hack"))
                .withAliasClue("Toto")
                .withIntroducerClue("Alice Tan")
                .withNoteClue("project teammate");
        FindCommand expectedFindCommand = new FindCommand(new PersonMatchesCluesPredicate(searchClues));

        assertParseSuccess(parser, "nt/project   teammate c/CS2103T a/Toto p/9123 "
                + "n/Tony Zhang i/Alice Tan e/@u.nus.edu c/Hack", expectedFindCommand);
    }

    @Test
    public void parse_nameOnly_returnsFindCommand() {
        FindCommand expectedFindCommand = new FindCommand(new PersonMatchesCluesPredicate(
                new PersonSearchClues().withNameClue("Tony")));

        assertParseSuccess(parser, "n/Tony", expectedFindCommand);
    }

    @Test
    public void parse_contextOnly_returnsFindCommand() {
        FindCommand expectedFindCommand = new FindCommand(new PersonMatchesCluesPredicate(
                new PersonSearchClues().withContextClues(List.of("CS2103T"))));

        assertParseSuccess(parser, "c/CS2103T", expectedFindCommand);
    }

    @Test
    public void parse_equivalentContextClues_collapsesDuplicates() {
        FindCommand expectedFindCommand = new FindCommand(new PersonMatchesCluesPredicate(
                new PersonSearchClues().withContextClues(List.of("CS2103T"))));

        assertParseSuccess(parser, "c/CS2103T c/cs2103t", expectedFindCommand);
    }

    @Test
    public void parse_emptyStructuredClue_throwsParseException() {
        assertParseFailure(parser, "n/", FindCommand.MESSAGE_EMPTY_NAME_CLUE);
        assertParseFailure(parser, "p/", FindCommand.MESSAGE_EMPTY_PHONE_CLUE);
        assertParseFailure(parser, "e/", FindCommand.MESSAGE_EMPTY_EMAIL_CLUE);
        assertParseFailure(parser, "c/", FindCommand.MESSAGE_EMPTY_CONTEXT_CLUE);
        assertParseFailure(parser, "a/", FindCommand.MESSAGE_EMPTY_ALIAS_CLUE);
        assertParseFailure(parser, "i/", FindCommand.MESSAGE_EMPTY_INTRODUCER_CLUE);
        assertParseFailure(parser, "nt/", FindCommand.MESSAGE_EMPTY_NOTE_CLUE);
        assertParseFailure(parser, "n/Tony c/", FindCommand.MESSAGE_EMPTY_CONTEXT_CLUE);
    }

    @Test
    public void parse_duplicateNamePrefix_throwsParseException() {
        assertParseFailure(parser, "n/Tony n/Zhang",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
    }

    @Test
    public void parse_duplicateSingleValuedPrefix_throwsParseException() {
        assertParseFailure(parser, "p/9123 p/8765",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));
        assertParseFailure(parser, "e/tony@example.com e/tony@u.nus.edu",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));
        assertParseFailure(parser, "a/Toto a/TZ",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ALIAS));
        assertParseFailure(parser, "i/Alice i/Bob",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_INTRODUCER));
        assertParseFailure(parser, "nt/project nt/friend",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NOTE));
    }

    @Test
    public void parse_mixedSearchModes_throwsParseException() {
        assertParseFailure(parser, "Tony c/CS2103T",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_slashTextInsideNote_returnsFindCommand() {
        FindCommand expectedFindCommand = new FindCommand(new PersonMatchesCluesPredicate(
                new PersonSearchClues().withNoteClue("pizza and/or burgers")));

        assertParseSuccess(parser, "nt/pizza and/or burgers", expectedFindCommand);
    }

    @Test
    public void parse_unsupportedOrUppercasePrefix_returnsKeywordSearch() {
        assertParseSuccess(parser, "x/value",
                new FindCommand(new PersonContainsKeywordsPredicate(List.of("x/value"))));
        assertParseSuccess(parser, "N/Tony",
                new FindCommand(new PersonContainsKeywordsPredicate(List.of("N/Tony"))));
        assertParseSuccess(parser, "P/91234567",
                new FindCommand(new PersonContainsKeywordsPredicate(List.of("P/91234567"))));
    }

}
