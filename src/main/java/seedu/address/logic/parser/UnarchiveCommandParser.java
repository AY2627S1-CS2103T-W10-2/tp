package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.UnarchiveByContextCommand;
import seedu.address.logic.commands.UnarchiveCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Context;

/**
 * Parses input arguments and creates a new command to unarchive by index or by context.
 */
public class UnarchiveCommandParser implements Parser<Command> {

    /**
     * Parses the given {@code String} of arguments in the context of the UnarchiveCommand
     * and returns the command for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public Command parse(String args) throws ParseException {
        Optional<Context> context = ContextArgumentParser.parse(args, UnarchiveCommand.MESSAGE_USAGE);
        if (context.isPresent()) {
            return new UnarchiveByContextCommand(context.get());
        }

        try {
            Index index = ParserUtil.parseIndex(args);
            return new UnarchiveCommand(index);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnarchiveCommand.MESSAGE_USAGE), pe);
        }
    }

}
