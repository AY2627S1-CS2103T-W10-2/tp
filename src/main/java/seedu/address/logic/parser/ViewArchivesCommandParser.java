package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.ViewArchivesCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ViewArchivesCommand object.
 * Unlike the other commands without parameters, it does not ignore text after the command word: the command word is
 * one letter away from {@code archive}, so {@code archives 1} is most likely a mistyped {@code archive 1}.
 */
public class ViewArchivesCommandParser implements Parser<ViewArchivesCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ViewArchivesCommand
     * and returns a ViewArchivesCommand object for execution.
     * @throws ParseException if anything but whitespace follows the command word
     */
    public ViewArchivesCommand parse(String args) throws ParseException {
        if (!args.trim().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewArchivesCommand.MESSAGE_USAGE));
        }
        return new ViewArchivesCommand();
    }

}
