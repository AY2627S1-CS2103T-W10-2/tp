package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.ArchiveCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.SetArchivedByContextCommand;
import seedu.address.logic.commands.UnarchiveCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Context;

/**
 * Parses input arguments and creates a new command to archive or unarchive, by index or by context. Archiving and
 * unarchiving are parsed the same way, so one parser serves both command words.
 */
public class ArchiveCommandParser implements Parser<Command> {

    private final boolean archived;

    /**
     * Creates a parser for the archive command if {@code archived} is true, or for the unarchive command if false.
     */
    public ArchiveCommandParser(boolean archived) {
        this.archived = archived;
    }

    /**
     * Parses the given {@code String} of arguments in the context of the archive or unarchive command
     * and returns the command for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public Command parse(String args) throws ParseException {
        String usage = archived ? ArchiveCommand.MESSAGE_USAGE : UnarchiveCommand.MESSAGE_USAGE;

        Optional<Context> context = ContextArgumentParser.parse(args, usage);
        if (context.isPresent()) {
            return new SetArchivedByContextCommand(context.get(), archived);
        }

        try {
            Index index = ParserUtil.parseIndex(args);
            return archived ? new ArchiveCommand(index) : new UnarchiveCommand(index);
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, usage), pe);
        }
    }

}
