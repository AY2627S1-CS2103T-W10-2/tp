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
import java.util.stream.Collectors;

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

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a person to the address book. "
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + PREFIX_CONTEXT + "CONTEXT "
            + "[" + PREFIX_CONTEXT + "CONTEXT]... "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ALIAS + "ALIAS] "
            + "[" + PREFIX_INTRODUCER + "INTRODUCER] "
            + "[" + PREFIX_NOTE + "NOTE]\n"
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
    public static final String MESSAGE_SIMILAR_PERSON_ARCHIVED_HINT =
            " This person is archived; run archives to see them.";

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

        Optional<Person> similarPerson = findSimilarPerson(model);
        model.addPerson(toAdd);
        String feedback = String.format(MESSAGE_SUCCESS, Messages.format(toAdd));
        if (similarPerson.isPresent()) {
            feedback += "\n" + formatSimilarPersonNote(model, similarPerson.get());
        }
        return new CommandResult(feedback);
    }

    /**
     * Returns the note telling the user that {@code similar} already exists. {@code model} must already
     * contain the person to add, so that the indexes shown match the list shown after the command.
     */
    private String formatSimilarPersonNote(Model model, Person similar) {
        String name = similar.getName().toString();
        String sharedContexts = similar.getContexts().stream()
                .filter(toAdd.getContexts()::contains)
                .map(Object::toString)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .map(context -> "[" + context + "]")
                .collect(Collectors.joining(" "));
        if (similar.isArchived()) {
            return String.format(MESSAGE_SIMILAR_PERSON_ARCHIVED_NOTE, name, sharedContexts)
                    + MESSAGE_SIMILAR_PERSON_ARCHIVED_HINT;
        }
        return String.format(MESSAGE_SIMILAR_PERSON_ACTIVE_NOTE, name, sharedContexts,
                getDisplayedIndex(model, similar), getDisplayedIndex(model, toAdd));
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
     * Returns an existing person in {@code model} with the same name (ignoring case and repeated spaces) and
     * at least one context in common with the person to add, if the person to add has no phone and no email.
     * An active person is preferred over an archived one, then the first in address book order.
     */
    private Optional<Person> findSimilarPerson(Model model) {
        if (!hasNoPhoneAndEmail(toAdd)) {
            return Optional.empty();
        }
        String normalisedName = normalise(toAdd.getName().fullName);
        return model.getAddressBook().getPersonList().stream()
                .filter(existing -> normalise(existing.getName().fullName).equals(normalisedName)
                        && !Collections.disjoint(existing.getContexts(), toAdd.getContexts()))
                .min(Comparator.comparing(Person::isArchived));
    }

    private static boolean hasNoPhoneAndEmail(Person person) {
        return person.getPhone().isEmpty() && person.getEmail().isEmpty();
    }

    private static String normalise(String name) {
        return name.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
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
