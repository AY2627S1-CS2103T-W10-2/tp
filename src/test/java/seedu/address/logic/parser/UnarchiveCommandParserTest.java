package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_EMPTY_CONTEXT;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.UnarchiveByContextCommand;
import seedu.address.logic.commands.UnarchiveCommand;
import seedu.address.model.person.Context;

public class UnarchiveCommandParserTest {

    private UnarchiveCommandParser parser = new UnarchiveCommandParser();

    @Test
    public void parse_validArgs_returnsCommand() {
        assertParseSuccess(parser, "1", new UnarchiveCommand(INDEX_FIRST_PERSON));
        assertParseSuccess(parser, "  1  ", new UnarchiveCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnarchiveCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "", expected);
        assertParseFailure(parser, "a", expected);
        assertParseFailure(parser, "0", expected);
        assertParseFailure(parser, "-1", expected);
        assertParseFailure(parser, "1 2", expected);
    }

    @Test
    public void parse_validContext_returnsByContextCommand() {
        UnarchiveByContextCommand expected = new UnarchiveByContextCommand(new Context("CS2103T"));
        assertParseSuccess(parser, " c/CS2103T", expected);
        assertParseSuccess(parser, "   c/  CS2103T  ", expected);
        assertParseSuccess(parser, " c/cs2103t", expected);
        assertParseSuccess(parser, " c/Hiking  club", new UnarchiveByContextCommand(new Context("Hiking club")));
    }

    @Test
    public void parse_invalidContextArgs_throwsParseException() {
        String expectedFormat = String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnarchiveCommand.MESSAGE_USAGE);

        // another prefix, in either order
        assertParseFailure(parser, " c/CS2103T n/John", expectedFormat);
        assertParseFailure(parser, " n/John c/CS2103T", expectedFormat);
        assertParseFailure(parser, " c/CS2103T p/91234567", expectedFormat);

        // more than one context
        assertParseFailure(parser, " c/CS2103T c/Hiking club", expectedFormat);

        // an index together with a context
        assertParseFailure(parser, "1 c/CS2103T", expectedFormat);

        // empty or invalid context
        assertParseFailure(parser, " c/", MESSAGE_EMPTY_CONTEXT);
        assertParseFailure(parser, " c/   ", MESSAGE_EMPTY_CONTEXT);
        assertParseFailure(parser, " c/(orbital)", Context.MESSAGE_CONSTRAINTS);
    }
}
