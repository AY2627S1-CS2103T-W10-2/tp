package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ALIAS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTEXT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INTRODUCER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;

import java.util.Locale;
import java.util.Optional;

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
    public static final String MESSAGE_IDENTICAL_PERSON_NOTE =
            "\nNote: %1$s with the same name and contexts already exists.";

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

        boolean hasIdenticalPerson = hasIdenticalPerson(model);
        model.addPerson(toAdd);
        String feedback = String.format(MESSAGE_SUCCESS, Messages.format(toAdd));
        if (hasIdenticalPerson) {
            feedback += String.format(MESSAGE_IDENTICAL_PERSON_NOTE, toAdd.getName());
        }
        return new CommandResult(feedback);
    }

    /**
     * Returns true if the person to add has no phone and no email, and {@code model} already contains
     * a person with no phone and no email, the same name (ignoring case and repeated spaces) and the same contexts.
     */
    private boolean hasIdenticalPerson(Model model) {
        if (!hasNoPhoneAndEmail(toAdd)) {
            return false;
        }
        return model.getAddressBook().getPersonList().stream()
                .filter(AddCommand::hasNoPhoneAndEmail)
                .anyMatch(existing -> normalise(existing.getName().fullName).equals(normalise(toAdd.getName().fullName))
                        && existing.getContexts().equals(toAdd.getContexts()));
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
