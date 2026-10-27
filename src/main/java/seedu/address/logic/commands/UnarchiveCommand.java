package seedu.address.logic.commands;

import seedu.address.commons.core.index.Index;

/**
 * Unarchives a person identified using its displayed index, so that it is in the normal person list again.
 */
public class UnarchiveCommand extends SetArchivedByIndexCommand {

    public static final String COMMAND_WORD = "unarchive";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Moves the archived person identified by the index number used in the displayed list "
            + "back to the normal person list, or every archived person with the given context. "
            + "Use the archives command to see archived persons first.\n"
            + "Parameters: INDEX (must be a positive integer) or c/CONTEXT\n"
            + "Examples: " + COMMAND_WORD + " 1, " + COMMAND_WORD + " c/CS2103T";

    /**
     * Creates an UnarchiveCommand to unarchive the person at the given displayed {@code targetIndex}.
     */
    public UnarchiveCommand(Index targetIndex) {
        super(targetIndex, false);
    }
}
