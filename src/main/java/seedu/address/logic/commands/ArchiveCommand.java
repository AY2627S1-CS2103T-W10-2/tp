package seedu.address.logic.commands;

import seedu.address.commons.core.index.Index;

/**
 * Archives a person identified using its displayed index. An archived person is kept in the address book but is
 * left out of the normal person list until it is unarchived.
 */
public class ArchiveCommand extends SetArchivedByIndexCommand {

    public static final String COMMAND_WORD = "archive";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Archives the person identified by the index number used in the displayed person list, "
            + "or every person with the given context.\n"
            + "Parameters: INDEX (must be a positive integer) or c/CONTEXT\n"
            + "Examples: " + COMMAND_WORD + " 1, " + COMMAND_WORD + " c/CS2103T";

    /**
     * Creates an ArchiveCommand to archive the person at the given displayed {@code targetIndex}.
     */
    public ArchiveCommand(Index targetIndex) {
        super(targetIndex, true);
    }
}
