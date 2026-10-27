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
 * Archives or unarchives a person identified using its displayed index. Archiving and unarchiving are the same
 * change with opposite values, so one class does both; the subclasses {@link ArchiveCommand} and
 * {@link UnarchiveCommand} only give the command word and the usage message.
 */
public class SetArchivedByIndexCommand extends Command {

    public static final String MESSAGE_ARCHIVE_PERSON_SUCCESS = "Archived person: %1$s";
    public static final String MESSAGE_ALREADY_ARCHIVED = "That person is already archived.";
    public static final String MESSAGE_UNARCHIVE_PERSON_SUCCESS = "Unarchived person: %1$s";
    public static final String MESSAGE_NOT_ARCHIVED = "That person is not archived. "
            + "Run archives to see archived persons, then use unarchive INDEX.";

    private final Index targetIndex;
    private final boolean archived;

    /**
     * Creates a command that sets the person at the given displayed {@code targetIndex} to {@code archived}.
     */
    protected SetArchivedByIndexCommand(Index targetIndex, boolean archived) {
        requireNonNull(targetIndex);
        this.targetIndex = targetIndex;
        this.archived = archived;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        // the index refers to the list on screen, so a person already in the target state cannot be changed
        Person personToChange = lastShownList.get(targetIndex.getZeroBased());
        if (personToChange.isArchived() == archived) {
            throw new CommandException(archived ? MESSAGE_ALREADY_ARCHIVED : MESSAGE_NOT_ARCHIVED);
        }

        Person changedPerson = personToChange.copyWithArchived(archived);
        model.setPerson(personToChange, changedPerson);
        String successMessage = archived ? MESSAGE_ARCHIVE_PERSON_SUCCESS : MESSAGE_UNARCHIVE_PERSON_SUCCESS;
        return new CommandResult(String.format(successMessage, Messages.format(changedPerson)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // getClass keeps an archive command from being equal to an unarchive command
        if (other == null || other.getClass() != getClass()) {
            return false;
        }

        SetArchivedByIndexCommand otherCommand = (SetArchivedByIndexCommand) other;
        return targetIndex.equals(otherCommand.targetIndex) && archived == otherCommand.archived;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
