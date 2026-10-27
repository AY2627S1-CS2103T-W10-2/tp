package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_EMPTY_CONTEXT;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ArchiveCommand;
import seedu.address.logic.commands.SetArchivedByContextCommand;
import seedu.address.model.person.Context;

public class ArchiveCommandParserTest {

    private ArchiveCommandParser parser = new ArchiveCommandParser(true);

    @Test
    public void parse_validArgs_returnsCommand() {
        assertParseSuccess(parser, "1", new ArchiveCommand(INDEX_FIRST_PERSON));
        assertParseSuccess(parser, "  1  ", new ArchiveCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ArchiveCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "", expected);
        assertParseFailure(parser, "a", expected);
        assertParseFailure(parser, "0", expected);
        assertParseFailure(parser, "-1", expected);
        assertParseFailure(parser, "1 2", expected);
    }

    @Test
    public void parse_validContext_returnsByContextCommand() {
        SetArchivedByContextCommand expected = new SetArchivedByContextCommand(new Context("CS2103T"), true);
        assertParseSuccess(parser, " c/CS2103T", expected);
        assertParseSuccess(parser, "   c/  CS2103T  ", expected);
        assertParseSuccess(parser, " c/cs2103t", expected);
        assertParseSuccess(parser, " c/Hiking  club",
                new SetArchivedByContextCommand(new Context("Hiking club"), true));
    }

    @Test
    public void parse_invalidContextArgs_throwsParseException() {
        String expectedFormat = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ArchiveCommand.MESSAGE_USAGE);

        // another prefix, in either order
        assertParseFailure(parser, " c/CS2103T n/John", expectedFormat);
        assertParseFailure(parser, " n/John c/CS2103T", expectedFormat);
        assertParseFailure(parser, " c/CS2103T p/91234567", expectedFormat);

        // more than one context
        assertParseFailure(parser, " c/CS2103T c/Hiking club", expectedFormat);

        // another command's signed prefixes
        assertParseFailure(parser, " c/CS2103T +c/CS2101", expectedFormat);
        assertParseFailure(parser, " c/CS2103T -c/CS2101", expectedFormat);
        assertParseFailure(parser, " c/CS2103T -p/", expectedFormat);
        assertParseFailure(parser, " c/CS2103T -e/", expectedFormat);
        assertParseFailure(parser, " -c/CS2101 c/CS2103T", expectedFormat);

        // an index together with a context
        assertParseFailure(parser, "1 c/CS2103T", expectedFormat);

        // empty or invalid context
        assertParseFailure(parser, " c/", MESSAGE_EMPTY_CONTEXT);
        assertParseFailure(parser, " c/   ", MESSAGE_EMPTY_CONTEXT);
        assertParseFailure(parser, " c/(orbital)", Context.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_tabBeforePrefix_throwsParseException() {
        // like every other command, a prefix only counts after a space
        String expectedFormat = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ArchiveCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "\tc/CS2103T", expectedFormat);
    }

    @Test
    public void parse_unknownPrefix_isNotTreatedAsPrefix() {
        String expectedFormat = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ArchiveCommand.MESSAGE_USAGE);

        // before c/ it is the preamble, after c/ it is part of the context
        assertParseFailure(parser, " x/foo c/CS2103T", expectedFormat);
        assertParseFailure(parser, " c/CS2103T x/foo", Context.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_everyOtherCliSyntaxPrefix_throwsParseException() throws Exception {
        String expectedFormat = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ArchiveCommand.MESSAGE_USAGE);

        // fails if a prefix is added to CliSyntax but not to ContextArgumentParser
        for (Field field : CliSyntax.class.getFields()) {
            if (!Modifier.isStatic(field.getModifiers()) || field.getType() != Prefix.class) {
                continue;
            }
            Prefix prefix = (Prefix) field.get(null);
            if (prefix.equals(CliSyntax.PREFIX_CONTEXT)) {
                continue;
            }
            assertParseFailure(parser, " c/CS2103T " + prefix + "x", expectedFormat);
        }
    }
}
