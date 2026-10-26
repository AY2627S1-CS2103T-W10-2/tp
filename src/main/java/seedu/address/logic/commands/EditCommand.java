package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTEXT_ADD;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTEXT_REMOVE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL_REMOVE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE_REMOVE;
import static seedu.address.model.Model.PREDICATE_SHOW_ACTIVE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Alias;
import seedu.address.model.person.Context;
import seedu.address.model.person.Email;
import seedu.address.model.person.Introducer;
import seedu.address.model.person.Name;
import seedu.address.model.person.Note;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

/**
 * Edits the details of an existing person in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits the person at the given index in the displayed list.\n"
            + "Parameters: INDEX (positive number shown beside the person in the list) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_PHONE_REMOVE + "] "
            + "[" + PREFIX_EMAIL_REMOVE + "] "
            + "[" + PREFIX_CONTEXT_ADD + "CONTEXT]... "
            + "[" + PREFIX_CONTEXT_REMOVE + "CONTEXT]...\n"
            + "Use " + PREFIX_PHONE_REMOVE + " or " + PREFIX_EMAIL_REMOVE + " to remove a phone or email.\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_CONTEXT_ADD + "NUS Hackers "
            + PREFIX_CONTEXT_REMOVE + "CS2040S";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited person: %1$s";
    public static final String MESSAGE_ARCHIVED_NOT_EDITABLE =
            "This person is archived and can't be edited. Unarchive the person first.";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";

    public static final String MESSAGE_CONTEXTS_NOT_FOUND_SKIPPED = "Note: %1$s does not have context(s): %2$s. "
            + "Check the spelling. Skipped.";
    public static final String MESSAGE_NO_CONTEXT_LEFT = "A person must have at least one context. "
            + "Add a new context with +c/ before removing the last one.";

    private final Index index;
    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * @param index of the person in the filtered person list to edit
     * @param editPersonDescriptor details to edit the person with
     */
    public EditCommand(Index index, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(index);
        requireNonNull(editPersonDescriptor);

        this.index = index;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());

        if (personToEdit.isArchived()) {
            throw new CommandException(MESSAGE_ARCHIVED_NOT_EDITABLE);
        }

        Person editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);

        Optional<Person> existingPerson = model.findDuplicate(editedPerson, personToEdit);
        if (existingPerson.isPresent()) {
            throw new CommandException(Messages.getErrorMessageForDuplicatePerson(existingPerson.get(), editedPerson));
        }

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ACTIVE);

        String feedback = String.format(MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));
        List<Context> unmatchedContexts = findUnmatchedContexts(personToEdit, editPersonDescriptor);
        if (!unmatchedContexts.isEmpty()) {
            feedback += "\n" + String.format(MESSAGE_CONTEXTS_NOT_FOUND_SKIPPED,
                    editedPerson.getName(), formatContexts(unmatchedContexts));
        }
        return new CommandResult(feedback);
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Person createEditedPerson(Person personToEdit, EditPersonDescriptor editPersonDescriptor)
            throws CommandException {
        assert personToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        Phone updatedPhone = editPersonDescriptor.isPhoneRemoved()
                ? null
                : editPersonDescriptor.getPhone().orElse(personToEdit.getPhone().orElse(null));
        Email updatedEmail = editPersonDescriptor.isEmailRemoved()
                ? null
                : editPersonDescriptor.getEmail().orElse(personToEdit.getEmail().orElse(null));
        Set<Context> updatedContexts = applyContextChanges(personToEdit, editPersonDescriptor);
        // edit cannot change these fields yet, so they are carried over unchanged
        Alias alias = personToEdit.getAlias().orElse(null);
        Introducer introducer = personToEdit.getIntroducer().orElse(null);
        Note note = personToEdit.getNote().orElse(null);

        return new Person(updatedName, updatedPhone, updatedEmail, updatedContexts, alias, introducer, note)
                .copyWithArchived(personToEdit.isArchived());
    }

    /**
     * Returns the contexts in {@code editPersonDescriptor} to remove that {@code personToEdit} does not have,
     * in the order they were given. Matching ignores case.
     */
    private static List<Context> findUnmatchedContexts(Person personToEdit,
            EditPersonDescriptor editPersonDescriptor) {
        assert personToEdit != null;

        Set<Context> existingContexts = personToEdit.getContexts();
        Set<Context> contextsToRemove = editPersonDescriptor.getContextsToRemove().orElse(Set.of());

        List<Context> unmatchedContexts = new ArrayList<>();
        for (Context context : contextsToRemove) {
            if (!existingContexts.contains(context)) {
                unmatchedContexts.add(context);
            }
        }
        return unmatchedContexts;
    }

    /**
     * Returns the contexts of {@code personToEdit} after removing and then adding the contexts in
     * {@code editPersonDescriptor}. A context the person does not have is not removed, and a context
     * the person already has is not added again.
     *
     * @throws CommandException if no context would remain.
     */
    private static Set<Context> applyContextChanges(Person personToEdit, EditPersonDescriptor editPersonDescriptor)
            throws CommandException {
        Set<Context> updatedContexts = new LinkedHashSet<>(personToEdit.getContexts());
        updatedContexts.removeAll(editPersonDescriptor.getContextsToRemove().orElse(Set.of()));
        // Set#add keeps the existing spelling when the person already has the context
        updatedContexts.addAll(editPersonDescriptor.getContextsToAdd().orElse(Set.of()));

        if (updatedContexts.isEmpty()) {
            throw new CommandException(MESSAGE_NO_CONTEXT_LEFT);
        }
        return updatedContexts;
    }

    /**
     * Formats {@code contexts} as {@code [A] [B]}, as in the person details shown to the user.
     */
    private static String formatContexts(List<Context> contexts) {
        return contexts.stream()
                .map(context -> "[" + context + "]")
                .collect(Collectors.joining(" "));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand otherEditCommand)) {
            return false;
        }

        return index.equals(otherEditCommand.index)
                && editPersonDescriptor.equals(otherEditCommand.editPersonDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editPersonDescriptor", editPersonDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Set<Context> contextsToAdd;
        private Set<Context> contextsToRemove;
        private boolean isPhoneRemoved;
        private boolean isEmailRemoved;

        public EditPersonDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code contexts} is used internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setContextsToAdd(toCopy.contextsToAdd);
            setContextsToRemove(toCopy.contextsToRemove);
            setPhoneRemoved(toCopy.isPhoneRemoved);
            setEmailRemoved(toCopy.isEmailRemoved);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, contextsToAdd, contextsToRemove)
                    || isPhoneRemoved || isEmailRemoved;
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setPhoneRemoved(boolean isPhoneRemoved) {
            this.isPhoneRemoved = isPhoneRemoved;
        }

        public boolean isPhoneRemoved() {
            return isPhoneRemoved;
        }

        public void setEmailRemoved(boolean isEmailRemoved) {
            this.isEmailRemoved = isEmailRemoved;
        }

        public boolean isEmailRemoved() {
            return isEmailRemoved;
        }

        /**
         * Sets {@code contextsToAdd} to this object's {@code contextsToAdd}.
         * A defensive copy of {@code contextsToAdd} is used internally.
         */
        public void setContextsToAdd(Set<Context> contextsToAdd) {
            this.contextsToAdd = (contextsToAdd != null) ? new LinkedHashSet<>(contextsToAdd) : null;
        }

        /**
         * Sets {@code contextsToRemove} to this object's {@code contextsToRemove}.
         * A defensive copy of {@code contextsToRemove} is used internally.
         */
        public void setContextsToRemove(Set<Context> contextsToRemove) {
            this.contextsToRemove = (contextsToRemove != null) ? new LinkedHashSet<>(contextsToRemove) : null;
        }

        /**
         * Returns an unmodifiable context set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code contextsToAdd} is null.
         */
        public Optional<Set<Context>> getContextsToAdd() {
            return (contextsToAdd != null) ? Optional.of(Collections.unmodifiableSet(contextsToAdd)) : Optional.empty();
        }

        /**
         * Returns an unmodifiable context set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code contextsToRemove} is null.
         */
        public Optional<Set<Context>> getContextsToRemove() {
            return (contextsToRemove != null)
                    ? Optional.of(Collections.unmodifiableSet(contextsToRemove))
                    : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor otherEditPersonDescriptor)) {
                return false;
            }

            return Objects.equals(name, otherEditPersonDescriptor.name)
                    && Objects.equals(phone, otherEditPersonDescriptor.phone)
                    && Objects.equals(email, otherEditPersonDescriptor.email)
                    && Objects.equals(contextsToAdd, otherEditPersonDescriptor.contextsToAdd)
                    && Objects.equals(contextsToRemove, otherEditPersonDescriptor.contextsToRemove)
                    && isPhoneRemoved == otherEditPersonDescriptor.isPhoneRemoved
                    && isEmailRemoved == otherEditPersonDescriptor.isEmailRemoved;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("email", email)
                    .add("contextsToAdd", contextsToAdd)
                    .add("contextsToRemove", contextsToRemove)
                    .add("isPhoneRemoved", isPhoneRemoved)
                    .add("isEmailRemoved", isEmailRemoved)
                    .toString();
        }
    }
}
