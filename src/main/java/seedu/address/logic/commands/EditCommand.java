package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ALIAS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ALIAS_REMOVE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTEXT_ADD;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTEXT_REMOVE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL_REMOVE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INTRODUCER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INTRODUCER_REMOVE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE_REMOVE;
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
            + "[" + PREFIX_ALIAS + "ALIAS] "
            + "[" + PREFIX_INTRODUCER + "INTRODUCER] "
            + "[" + PREFIX_NOTE + "NOTE] "
            + "[" + PREFIX_PHONE_REMOVE + "] "
            + "[" + PREFIX_EMAIL_REMOVE + "] "
            + "[" + PREFIX_ALIAS_REMOVE + "] "
            + "[" + PREFIX_INTRODUCER_REMOVE + "] "
            + "[" + PREFIX_NOTE_REMOVE + "] "
            + "[" + PREFIX_CONTEXT_ADD + "CONTEXT]... "
            + "[" + PREFIX_CONTEXT_REMOVE + "CONTEXT]...\n"
            + "Use " + PREFIX_PHONE_REMOVE + ", " + PREFIX_EMAIL_REMOVE + ", " + PREFIX_ALIAS_REMOVE + ", "
            + PREFIX_INTRODUCER_REMOVE + " or " + PREFIX_NOTE_REMOVE
            + " to remove the phone, email, alias, introducer or note.\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_CONTEXT_ADD + "NUS Hackers "
            + PREFIX_CONTEXT_REMOVE + "CS2040S";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited person: %1$s";
    public static final String MESSAGE_ARCHIVED_NOT_EDITABLE =
            "This person is archived and can't be edited. Unarchive the person first.";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";

    public static final String MESSAGE_CONTEXTS_NOT_FOUND = "%1$s does not have context(s): %2$s. Check the spelling.";
    public static final String MESSAGE_NO_CONTEXT_LEFT = "A person must have at least one context. "
            + "Add a new context with +c/ before removing the last one.";
    public static final String MESSAGE_CONTEXTS_SKIPPED = "Note: %1$s already has context(s): %2$s. Skipped.";

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

        List<Context> unmatchedContexts = findUnmatchedContexts(personToEdit, editPersonDescriptor);
        if (!unmatchedContexts.isEmpty()) {
            throw new CommandException(String.format(MESSAGE_CONTEXTS_NOT_FOUND,
                    personToEdit.getName(), formatContexts(unmatchedContexts)));
        }

        Person editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);

        Optional<Person> existingPerson = model.findDuplicate(editedPerson, personToEdit);
        if (existingPerson.isPresent()) {
            throw new CommandException(Messages.getErrorMessageForDuplicatePerson(existingPerson.get(), editedPerson));
        }

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ACTIVE);

        String feedback = String.format(MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));
        List<Context> skippedContexts = findSkippedContexts(personToEdit, editPersonDescriptor);
        if (!skippedContexts.isEmpty()) {
            feedback += "\n" + String.format(MESSAGE_CONTEXTS_SKIPPED,
                    editedPerson.getName(), formatContexts(skippedContexts));
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
        Phone updatedPhone = applyOptionalFieldEdit(editPersonDescriptor.getPhone(),
                editPersonDescriptor.isPhoneRemoved(), personToEdit.getPhone());
        Email updatedEmail = applyOptionalFieldEdit(editPersonDescriptor.getEmail(),
                editPersonDescriptor.isEmailRemoved(), personToEdit.getEmail());
        Set<Context> updatedContexts = applyContextChanges(personToEdit, editPersonDescriptor);
        Alias updatedAlias = applyOptionalFieldEdit(editPersonDescriptor.getAlias(),
                editPersonDescriptor.isAliasRemoved(), personToEdit.getAlias());
        Introducer updatedIntroducer = applyOptionalFieldEdit(editPersonDescriptor.getIntroducer(),
                editPersonDescriptor.isIntroducerRemoved(), personToEdit.getIntroducer());
        Note updatedNote = applyOptionalFieldEdit(editPersonDescriptor.getNote(),
                editPersonDescriptor.isNoteRemoved(), personToEdit.getNote());

        return new Person(updatedName, updatedPhone, updatedEmail, updatedContexts,
                updatedAlias, updatedIntroducer, updatedNote)
                .copyWithArchived(personToEdit.isArchived());
    }

    /**
     * Returns the value of an optional field after an edit: {@code null} if the field is removed,
     * otherwise {@code newValue} if given, otherwise {@code currentValue}. {@code null} means no value.
     */
    private static <T> T applyOptionalFieldEdit(Optional<T> newValue, boolean isRemoved,
            Optional<T> currentValue) {
        if (isRemoved) {
            return null;
        }
        return newValue.orElse(currentValue.orElse(null));
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
     * Returns the contexts in {@code editPersonDescriptor} to add that {@code personToEdit} still has after
     * the removals, in the order they were given. Each is spelt the way the person has it.
     */
    private static List<Context> findSkippedContexts(Person personToEdit,
            EditPersonDescriptor editPersonDescriptor) {
        assert personToEdit != null;

        Set<Context> keptContexts = new LinkedHashSet<>(personToEdit.getContexts());
        keptContexts.removeAll(editPersonDescriptor.getContextsToRemove().orElse(Set.of()));
        Set<Context> contextsToAdd = editPersonDescriptor.getContextsToAdd().orElse(Set.of());

        List<Context> skippedContexts = new ArrayList<>();
        for (Context contextToAdd : contextsToAdd) {
            // Context#equals ignores case, so look up the kept context to get the person's spelling
            keptContexts.stream()
                    .filter(contextToAdd::equals)
                    .findFirst()
                    .ifPresent(skippedContexts::add);
        }
        return skippedContexts;
    }

    /**
     * Returns the contexts of {@code personToEdit} after removing and then adding the contexts in
     * {@code editPersonDescriptor}. A context the person already has is not added again.
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
        private Alias alias;
        private Introducer introducer;
        private Note note;
        private Set<Context> contextsToAdd;
        private Set<Context> contextsToRemove;
        private boolean isPhoneRemoved;
        private boolean isEmailRemoved;
        private boolean isAliasRemoved;
        private boolean isIntroducerRemoved;
        private boolean isNoteRemoved;

        public EditPersonDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code contexts} is used internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setAlias(toCopy.alias);
            setIntroducer(toCopy.introducer);
            setNote(toCopy.note);
            setContextsToAdd(toCopy.contextsToAdd);
            setContextsToRemove(toCopy.contextsToRemove);
            setPhoneRemoved(toCopy.isPhoneRemoved);
            setEmailRemoved(toCopy.isEmailRemoved);
            setAliasRemoved(toCopy.isAliasRemoved);
            setIntroducerRemoved(toCopy.isIntroducerRemoved);
            setNoteRemoved(toCopy.isNoteRemoved);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, alias, introducer, note,
                    contextsToAdd, contextsToRemove)
                    || isPhoneRemoved || isEmailRemoved
                    || isAliasRemoved || isIntroducerRemoved || isNoteRemoved;
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

        public void setAlias(Alias alias) {
            this.alias = alias;
        }

        public Optional<Alias> getAlias() {
            return Optional.ofNullable(alias);
        }

        public void setIntroducer(Introducer introducer) {
            this.introducer = introducer;
        }

        public Optional<Introducer> getIntroducer() {
            return Optional.ofNullable(introducer);
        }

        public void setNote(Note note) {
            this.note = note;
        }

        public Optional<Note> getNote() {
            return Optional.ofNullable(note);
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

        public void setAliasRemoved(boolean isAliasRemoved) {
            this.isAliasRemoved = isAliasRemoved;
        }

        public boolean isAliasRemoved() {
            return isAliasRemoved;
        }

        public void setIntroducerRemoved(boolean isIntroducerRemoved) {
            this.isIntroducerRemoved = isIntroducerRemoved;
        }

        public boolean isIntroducerRemoved() {
            return isIntroducerRemoved;
        }

        public void setNoteRemoved(boolean isNoteRemoved) {
            this.isNoteRemoved = isNoteRemoved;
        }

        public boolean isNoteRemoved() {
            return isNoteRemoved;
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
                    && Objects.equals(alias, otherEditPersonDescriptor.alias)
                    && Objects.equals(introducer, otherEditPersonDescriptor.introducer)
                    && Objects.equals(note, otherEditPersonDescriptor.note)
                    && Objects.equals(contextsToAdd, otherEditPersonDescriptor.contextsToAdd)
                    && Objects.equals(contextsToRemove, otherEditPersonDescriptor.contextsToRemove)
                    && isPhoneRemoved == otherEditPersonDescriptor.isPhoneRemoved
                    && isEmailRemoved == otherEditPersonDescriptor.isEmailRemoved
                    && isAliasRemoved == otherEditPersonDescriptor.isAliasRemoved
                    && isIntroducerRemoved == otherEditPersonDescriptor.isIntroducerRemoved
                    && isNoteRemoved == otherEditPersonDescriptor.isNoteRemoved;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("email", email)
                    .add("alias", alias)
                    .add("introducer", introducer)
                    .add("note", note)
                    .add("contextsToAdd", contextsToAdd)
                    .add("contextsToRemove", contextsToRemove)
                    .add("isPhoneRemoved", isPhoneRemoved)
                    .add("isEmailRemoved", isEmailRemoved)
                    .add("isAliasRemoved", isAliasRemoved)
                    .add("isIntroducerRemoved", isIntroducerRemoved)
                    .add("isNoteRemoved", isNoteRemoved)
                    .toString();
        }
    }
}
