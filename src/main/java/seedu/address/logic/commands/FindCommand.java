package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ACTIVE;

import java.util.function.Predicate;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Finds and lists all persons matching the supplied search predicate.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Finds persons using keywords or field-specific clues.\n"
            + "Parameters:\n"
            + "  KEYWORD [MORE_KEYWORDS]...\n"
            + "  [n/NAME_PART] [p/PHONE_PART] [e/EMAIL_PART] [c/CONTEXT_PART]... "
            + "[a/ALIAS_PART] [i/INTRODUCER_PART] [nt/NOTE_PART]\n"
            + "nt/NOTE_PART must come last: it takes the rest of the line.\n"
            + "Example: " + COMMAND_WORD + " n/Alex c/CS2103T nt/project";

    public static final String MESSAGE_NO_PERSONS_FOUND = "No persons found!";
    public static final String MESSAGE_EMPTY_NAME_CLUE = "Name search clue cannot be empty.";
    public static final String MESSAGE_EMPTY_PHONE_CLUE = "Phone search clue cannot be empty.";
    public static final String MESSAGE_EMPTY_EMAIL_CLUE = "Email search clue cannot be empty.";
    public static final String MESSAGE_EMPTY_CONTEXT_CLUE = "Context search clue cannot be empty.";
    public static final String MESSAGE_EMPTY_ALIAS_CLUE = "Alias search clue cannot be empty.";
    public static final String MESSAGE_EMPTY_INTRODUCER_CLUE = "Introducer search clue cannot be empty.";
    public static final String MESSAGE_EMPTY_NOTE_CLUE = "Note search clue cannot be empty.";

    private static final Logger logger = LogsCenter.getLogger(FindCommand.class);

    private final Predicate<Person> predicate;

    /**
     * Creates a FindCommand that filters persons using {@code predicate}.
     */
    public FindCommand(Predicate<Person> predicate) {
        this.predicate = requireNonNull(predicate);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        // archived persons are never found; they are only shown by the archives command
        model.updateFilteredPersonList(PREDICATE_SHOW_ACTIVE.and(predicate));
        int matchCount = model.getFilteredPersonList().size();
        logger.fine("Find command matched " + matchCount + " active person(s).");
        if (matchCount == 0) {
            return new CommandResult(MESSAGE_NO_PERSONS_FOUND);
        }
        String message = matchCount == 1
                ? ListCommand.MESSAGE_SUCCESS_SINGULAR : ListCommand.MESSAGE_SUCCESS_PLURAL;
        return new CommandResult(String.format(message, matchCount));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand otherFindCommand)) {
            return false;
        }

        return predicate.equals(otherFindCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
