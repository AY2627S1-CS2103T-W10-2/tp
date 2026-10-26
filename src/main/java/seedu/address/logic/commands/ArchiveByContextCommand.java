package seedu.address.logic.commands;

import seedu.address.model.person.Context;

/**
 * Archives every person who is not archived yet and has a given context.
 */
public class ArchiveByContextCommand extends SetArchivedByContextCommand {

    public static final String MESSAGE_ARCHIVE_SUCCESS = "Archived %1$d person(s) with context %2$s.";
    public static final String MESSAGE_NO_PERSONS_TO_ARCHIVE = "No persons to archive with context %1$s.";

    /**
     * Creates an ArchiveByContextCommand to archive the persons who have the given {@code context}.
     */
    public ArchiveByContextCommand(Context context) {
        super(context);
    }

    @Override
    protected boolean getTargetArchivedState() {
        return true;
    }

    @Override
    protected String getNoMatchMessage(Context context) {
        return String.format(MESSAGE_NO_PERSONS_TO_ARCHIVE, context);
    }

    @Override
    protected String getSuccessMessage(int count, Context context) {
        return String.format(MESSAGE_ARCHIVE_SUCCESS, count, context);
    }
}
