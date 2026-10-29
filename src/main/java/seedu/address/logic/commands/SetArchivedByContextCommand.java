package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Context;
import seedu.address.model.person.Person;

/**
 * Archives or unarchives every person in the address book who has a given context. Archiving and unarchiving are
 * the same change with opposite values, so one class does both. The whole address book is used, not the displayed
 * list, so the result does not depend on any earlier find, list or archives command.
 * The index form of the same commands is handled by {@link SetArchivedByIndexCommand}. The two forms differ on
 * purpose: here the user asks for a group, so persons already in the target state are skipped and the command fails
 * only if no person changes, whereas that class acts on one displayed person and treats such a person as an error.
 */
public class SetArchivedByContextCommand extends Command {

    public static final String MESSAGE_ARCHIVE_SUCCESS = "Archived %1$d person(s) with context %2$s.";
    public static final String MESSAGE_NO_PERSONS_TO_ARCHIVE = "No persons to archive with context %1$s.";
    public static final String MESSAGE_UNARCHIVE_SUCCESS = "Unarchived %1$d person(s) with context %2$s.";
    public static final String MESSAGE_NO_PERSONS_TO_UNARCHIVE = "No archived persons with context %1$s.";

    private final Context context;
    private final boolean archived;

    /**
     * Creates a command that sets the persons who have the given {@code context} to {@code archived}.
     */
    public SetArchivedByContextCommand(Context context, boolean archived) {
        requireNonNull(context);
        this.context = context;
        this.archived = archived;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        // copied first, because setPerson changes the list that is being read
        List<Person> targets = List.copyOf(model.getAddressBook().getPersonList()).stream()
                .filter(person -> person.getContexts().contains(context) && person.isArchived() != archived)
                .toList();

        if (targets.isEmpty()) {
            String noMatchMessage = archived ? MESSAGE_NO_PERSONS_TO_ARCHIVE : MESSAGE_NO_PERSONS_TO_UNARCHIVE;
            throw new CommandException(String.format(noMatchMessage, context));
        }

        for (Person person : targets) {
            model.setPerson(person, person.copyWithArchived(archived));
        }
        String successMessage = archived ? MESSAGE_ARCHIVE_SUCCESS : MESSAGE_UNARCHIVE_SUCCESS;
        return new CommandResult(String.format(successMessage, targets.size(), context));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof SetArchivedByContextCommand otherCommand)) {
            return false;
        }

        return context.equals(otherCommand.context) && archived == otherCommand.archived;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("context", context)
                .add("archived", archived)
                .toString();
    }
}
