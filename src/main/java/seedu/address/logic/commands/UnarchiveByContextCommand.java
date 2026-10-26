package seedu.address.logic.commands;

import seedu.address.model.person.Context;

/**
 * Unarchives every archived person who has a given context.
 */
public class UnarchiveByContextCommand extends SetArchivedByContextCommand {

    public static final String MESSAGE_UNARCHIVE_SUCCESS = "Unarchived %1$d person(s) with context %2$s.";
    public static final String MESSAGE_NO_PERSONS_TO_UNARCHIVE = "No archived persons with context %1$s.";

    /**
     * Creates an UnarchiveByContextCommand to unarchive the archived persons who have the given {@code context}.
     */
    public UnarchiveByContextCommand(Context context) {
        super(context);
    }

    @Override
    protected boolean getTargetArchivedState() {
        return false;
    }

    @Override
    protected String getNoMatchMessage(Context context) {
        return String.format(MESSAGE_NO_PERSONS_TO_UNARCHIVE, context);
    }

    @Override
    protected String getSuccessMessage(int count, Context context) {
        return String.format(MESSAGE_UNARCHIVE_SUCCESS, count, context);
    }
}
