package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Unarchives a person identified using its displayed index, so that it is in the normal person list again.
 */
public class UnarchiveCommand extends Command {

    public static final String COMMAND_WORD = "unarchive";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Moves the archived person identified by the index number used in the displayed list "
            + "back to the normal person list, or every archived person with the given context. "
            + "Use the archives command to see archived persons first.\n"
            + "Parameters: INDEX (must be a positive integer) or c/CONTEXT\n"
            + "Examples: " + COMMAND_WORD + " 1, " + COMMAND_WORD + " c/CS2103T";

    public static final String MESSAGE_UNARCHIVE_PERSON_SUCCESS = "Unarchived person: %1$s";
    public static final String MESSAGE_NOT_ARCHIVED = "That person is not archived. "
            + "Run archives to see archived persons, then use unarchive INDEX.";

    private final Index targetIndex;

    /**
     * Creates an UnarchiveCommand to unarchive the person at the given displayed {@code targetIndex}.
     */
    public UnarchiveCommand(Index targetIndex) {
        requireNonNull(targetIndex);
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        // the index refers to the list on screen, so only a person who is really archived can be unarchived
        Person personToUnarchive = lastShownList.get(targetIndex.getZeroBased());
        if (!personToUnarchive.isArchived()) {
            throw new CommandException(MESSAGE_NOT_ARCHIVED);
        }

        Person unarchivedPerson = personToUnarchive.copyWithArchived(false);
        model.setPerson(personToUnarchive, unarchivedPerson);
        return new CommandResult(String.format(MESSAGE_UNARCHIVE_PERSON_SUCCESS, Messages.format(unarchivedPerson)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof UnarchiveCommand otherUnarchiveCommand)) {
            return false;
        }

        return targetIndex.equals(otherUnarchiveCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
