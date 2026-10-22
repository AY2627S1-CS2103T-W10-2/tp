package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ARCHIVED;

import seedu.address.model.Model;

/**
 * Shows the archived persons. Use the list command to go back to the normal person list.
 */
public class ViewArchivesCommand extends Command {

    public static final String COMMAND_WORD = "archives";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows the archived persons. "
            + "This command takes no parameters; to archive a person, use archive INDEX.\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS_SINGULAR = "Listed %1$d archived person.";
    public static final String MESSAGE_SUCCESS_PLURAL = "Listed %1$d archived persons.";
    public static final String MESSAGE_NO_ARCHIVED_PERSONS = "No archived persons.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(PREDICATE_SHOW_ARCHIVED);

        int count = model.getFilteredPersonList().size();
        if (count == 0) {
            return new CommandResult(MESSAGE_NO_ARCHIVED_PERSONS);
        }
        String message = count == 1 ? MESSAGE_SUCCESS_SINGULAR : MESSAGE_SUCCESS_PLURAL;
        return new CommandResult(String.format(message, count));
    }
}
