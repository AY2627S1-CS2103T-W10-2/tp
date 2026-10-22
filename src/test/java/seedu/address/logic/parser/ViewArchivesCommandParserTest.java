package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ViewArchivesCommand;

public class ViewArchivesCommandParserTest {

    private ViewArchivesCommandParser parser = new ViewArchivesCommandParser();

    @Test
    public void parse_noArguments_returnsCommand() throws Exception {
        assertTrue(parser.parse("") instanceof ViewArchivesCommand);
        assertTrue(parser.parse("   ") instanceof ViewArchivesCommand);
        assertTrue(parser.parse("\t") instanceof ViewArchivesCommand);
    }

    @Test
    public void parse_extraText_throwsParseException() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewArchivesCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " 1", expected);
        assertParseFailure(parser, " foo", expected);
        assertParseFailure(parser, " 1 2", expected);
    }
}
