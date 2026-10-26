package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.ArchiveByContextCommand;
import seedu.address.logic.commands.ArchiveCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Context;

/**
 * Parses input arguments and creates a new command to archive by index or by context.
 */
public class ArchiveCommandParser implements Parser<Command> {

    /**
     * Parses the given {@code String} of arguments in the context of the ArchiveCommand
     * and returns the command for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public Command parse(String args) throws ParseException {
        Optional<Context> context = ContextArgumentParser.parse(args, ArchiveCommand.MESSAGE_USAGE);
        if (context.isPresent()) {
            return new ArchiveByContextCommand(context.get());
        }

        try {
            Index index = ParserUtil.parseIndex(args);
            return new ArchiveCommand(index);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ArchiveCommand.MESSAGE_USAGE), pe);
        }
    }

}
