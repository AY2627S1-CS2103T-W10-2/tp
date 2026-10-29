package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ContextsCommand;

public class ContextsCommandParserTest {

    private final ContextsCommandParser parser = new ContextsCommandParser();

    @Test
    public void parse_noArguments_returnsCommand() throws Exception {
        for (String input : List.of("", "   ", "\t")) {
            assertTrue(parser.parse(input) instanceof ContextsCommand);
        }
    }

    @Test
    public void parse_arguments_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ContextsCommand.MESSAGE_USAGE);
        for (String input : List.of(" CS2103T", " c/CS2103T", " 1", " archived", " CS2103T NUS Hackers")) {
            assertParseFailure(parser, input, expectedMessage);
        }
    }
}
