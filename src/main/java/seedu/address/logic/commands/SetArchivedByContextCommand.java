package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Context;
import seedu.address.model.person.Person;

/**
 * Archives or unarchives every person in the address book who has a given context. The subclasses decide which of
 * the two it is. The whole address book is used, not the displayed list, so the result does not depend on any
 * earlier find, list or archives command.
 */
public abstract class SetArchivedByContextCommand extends Command {

    private final Context context;

    /**
     * Creates a command that changes the archived state of the persons who have the given {@code context}.
     */
    protected SetArchivedByContextCommand(Context context) {
        requireNonNull(context);
        this.context = context;
    }

    /** Returns the archived state that the matching persons are changed to. */
    protected abstract boolean getTargetArchivedState();

    /** Returns the message shown when no person with {@code context} can be changed. */
    protected abstract String getNoMatchMessage(Context context);

    /** Returns the message shown after {@code count} persons with {@code context} were changed. */
    protected abstract String getSuccessMessage(int count, Context context);

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        boolean targetState = getTargetArchivedState();

        // copied first, because setPerson changes the list that is being read
        List<Person> targets = List.copyOf(model.getAddressBook().getPersonList()).stream()
                .filter(person -> person.getContexts().contains(context) && person.isArchived() != targetState)
                .toList();

        if (targets.isEmpty()) {
            throw new CommandException(getNoMatchMessage(context));
        }

        for (Person person : targets) {
            model.setPerson(person, person.copyWithArchived(targetState));
        }
        return new CommandResult(getSuccessMessage(targets.size(), context));
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

        return context.equals(((SetArchivedByContextCommand) other).context);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("context", context)
                .toString();
    }
}
