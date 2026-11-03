package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ClearCommand;

public class ClearCommandParserTest {

    private ClearCommandParser parser = new ClearCommandParser();

    @Test
    public void parse_noArguments_returnsCommand() throws Exception {
        assertTrue(parser.parse("") instanceof ClearCommand);
        assertTrue(parser.parse("   ") instanceof ClearCommand);
        assertTrue(parser.parse("\t") instanceof ClearCommand);
    }

    @Test
    public void parse_extraText_throwsParseException() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ClearCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " 1", expected);
        assertParseFailure(parser, " Alice", expected);
        assertParseFailure(parser, " 1 2", expected);
    }
}
