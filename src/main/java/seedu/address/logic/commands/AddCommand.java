package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ALIAS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTEXT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INTRODUCER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Adds a person to the address book.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a person to UniContacts. "
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + PREFIX_CONTEXT + "CONTEXT "
            + "[" + PREFIX_CONTEXT + "CONTEXT]... "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ALIAS + "ALIAS] "
            + "[" + PREFIX_INTRODUCER + "INTRODUCER] "
            + "[" + PREFIX_NOTE + "NOTE]\n"
            + PREFIX_NOTE + "NOTE must come last: it takes the rest of the line.\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "John Doe "
            + PREFIX_CONTEXT + "CS2103T "
            + PREFIX_PHONE + "98765432 "
            + PREFIX_EMAIL + "johnd@example.com";

    public static final String MESSAGE_SUCCESS = "New person added: %1$s";
    public static final String MESSAGE_SIMILAR_PERSON_ACTIVE_NOTE =
            "Note: %1$s %2$s already exists at index %3$d. "
            + "If this is the same person, run delete %4$d to remove the new entry and use edit %3$d instead.";
    public static final String MESSAGE_SIMILAR_PERSON_ARCHIVED_NOTE =
            "Note: %1$s %2$s already exists.";

    private static final Logger logger = LogsCenter.getLogger(AddCommand.class);

    private final Person toAdd;

    /**
     * Creates an AddCommand to add the specified {@code Person}
     */
    public AddCommand(Person person) {
        requireNonNull(person);
        toAdd = person;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Optional<Person> existingPerson = model.findDuplicate(toAdd);
        if (existingPerson.isPresent()) {
            throw new CommandException(Messages.getErrorMessageForDuplicatePerson(existingPerson.get(), toAdd));
        }

        Optional<Person> similarPerson = findSimilarPerson(model, toAdd);
        model.addPerson(toAdd);
        logger.fine("Added " + toAdd.getName());
        String feedback = String.format(MESSAGE_SUCCESS, Messages.format(toAdd));
        if (similarPerson.isPresent()) {
            String status = similarPerson.get().isArchived() ? "archived" : "active";
            logger.info("Added person but a similar " + status + " person already exists");
            feedback += "\n" + formatSimilarPersonNote(model, similarPerson.get(), toAdd,
                    MESSAGE_SIMILAR_PERSON_ACTIVE_NOTE);
        }
        return new CommandResult(feedback);
    }

    /**
     * Returns the note telling the user that {@code similar} already exists. {@code model} must already
     * contain {@code person}, so that the indexes shown match the list shown after the command.
     * {@code activeNote} is filled with the name, shared contexts, index of {@code similar} and index of
     * {@code person}, in that order.
     */
    static String formatSimilarPersonNote(Model model, Person similar, Person person, String activeNote) {
        String name = similar.getName().toString();
        String sharedContexts = Messages.formatContextsSorted(similar.getContexts().stream()
                .filter(person.getContexts()::contains)
                .toList());
        if (similar.isArchived()) {
            return String.format(MESSAGE_SIMILAR_PERSON_ARCHIVED_NOTE, name, sharedContexts)
                    + " " + Messages.MESSAGE_ARCHIVED_PERSON_HINT;
        }
        return String.format(activeNote, name, sharedContexts,
                getDisplayedIndex(model, similar), getDisplayedIndex(model, person));
    }

    /**
     * Returns the one-based index of {@code person} in the list shown to the user. The very same object is
     * looked for, because two persons without a phone number or email can be equal.
     */
    private static int getDisplayedIndex(Model model, Person person) {
        List<Person> shownList = model.getFilteredPersonList();
        for (int i = 0; i < shownList.size(); i++) {
            if (shownList.get(i) == person) {
                return i + 1;
            }
        }
        throw new AssertionError("The person should be in the shown list");
    }

    /**
     * Returns another person in {@code model} with the same name (ignoring case and repeated spaces) and
     * at least one context in common with {@code person}, whatever their phone numbers and emails.
     * An active person is preferred over an archived one, then the first in address book order.
     */
    static Optional<Person> findSimilarPerson(Model model, Person person) {
        String normalisedName = normalise(person.getName().fullName);
        // the very same object is skipped, so an edited person is not similar to themselves
        return model.getAddressBook().getPersonList().stream()
                .filter(existing -> existing != person
                        && normalise(existing.getName().fullName).equals(normalisedName)
                        && !Collections.disjoint(existing.getContexts(), person.getContexts()))
                .min(Comparator.comparing(Person::isArchived));
    }

    private static String normalise(String name) {
        return StringUtil.collapseWhitespace(name).toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddCommand otherAddCommand)) {
            return false;
        }

        return toAdd.equals(otherAddCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
