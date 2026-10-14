package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import seedu.address.model.Model;

/**
 * Lists all persons in the address book to the user.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_SUCCESS_SINGULAR = "%1$d person listed.";
    public static final String MESSAGE_SUCCESS_PLURAL = "%1$d people listed.";
    public static final String MESSAGE_SUCCESS = MESSAGE_SUCCESS_PLURAL;


    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        int personCount = model.getFilteredPersonList().size();
        String message = personCount == 1 ? MESSAGE_SUCCESS_SINGULAR : MESSAGE_SUCCESS_PLURAL;
        return new CommandResult(String.format(message, personCount));
    }
}
