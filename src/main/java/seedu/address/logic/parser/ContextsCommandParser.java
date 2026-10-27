package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.ContextsCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses the context overview request, rejecting arguments so it cannot be mistaken for a context search.
 */
public class ContextsCommandParser implements Parser<ContextsCommand> {

    /**
     * Returns a context overview command if no arguments were supplied.
     *
     * @throws ParseException if anything but whitespace follows the command word
     */
    @Override
    public ContextsCommand parse(String args) throws ParseException {
        if (!args.trim().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ContextsCommand.MESSAGE_USAGE));
        }
        return new ContextsCommand();
    }
}
