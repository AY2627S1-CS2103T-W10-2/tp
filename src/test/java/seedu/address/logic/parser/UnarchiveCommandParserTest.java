package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.SetArchivedByContextCommand;
import seedu.address.logic.commands.UnarchiveCommand;
import seedu.address.model.person.Context;

public class UnarchiveCommandParserTest {

    private ArchiveCommandParser parser = new ArchiveCommandParser(false);

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
        // the c/ rules are tested in ArchiveCommandParserTest, as both command words use the same parser
        assertParseSuccess(parser, " c/CS2103T", new SetArchivedByContextCommand(new Context("CS2103T"), false));
    }

    @Test
    public void parse_invalidContextArgs_showsUnarchiveUsage() {
        String expectedFormat = String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnarchiveCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " c/CS2103T n/John", expectedFormat);
    }
}
