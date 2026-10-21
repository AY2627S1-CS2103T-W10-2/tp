package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ACTIVE;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Clears the contacts that are not archived. Archived contacts are kept, so that they are not lost by accident.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_SUCCESS = "Address book has been cleared!";
    public static final String MESSAGE_SUCCESS_KEEPING_ONE_ARCHIVED = "Contacts cleared. 1 archived contact was kept.";
    public static final String MESSAGE_SUCCESS_KEEPING_ARCHIVED = "Contacts cleared. %1$d archived contacts were kept.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        AddressBook archivedOnly = new AddressBook();
        model.getAddressBook().getPersonList().stream()
                .filter(Person::isArchived)
                .forEach(archivedOnly::addPerson);
        model.setAddressBook(archivedOnly);
        model.updateFilteredPersonList(PREDICATE_SHOW_ACTIVE);

        int keptCount = archivedOnly.getPersonList().size();
        if (keptCount == 0) {
            return new CommandResult(MESSAGE_SUCCESS);
        }
        if (keptCount == 1) {
            return new CommandResult(MESSAGE_SUCCESS_KEEPING_ONE_ARCHIVED);
        }
        return new CommandResult(String.format(MESSAGE_SUCCESS_KEEPING_ARCHIVED, keptCount));
    }
}
