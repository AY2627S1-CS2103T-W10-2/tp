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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            + "[" + PREFIX_PHONE_REMOVE + "] "
            + "[" + PREFIX_EMAIL_REMOVE + "] "
            + "[" + PREFIX_ALIAS_REMOVE + "] "
            + "[" + PREFIX_INTRODUCER_REMOVE + "] "
            + "[" + PREFIX_NOTE_REMOVE + "] "
            + "[" + PREFIX_CONTEXT_ADD + "CONTEXT]... "
            + "[" + PREFIX_CONTEXT_REMOVE + "CONTEXT]... "
            + "[" + PREFIX_NOTE + "NOTE]\n"
            + "Use " + PREFIX_PHONE_REMOVE + ", " + PREFIX_EMAIL_REMOVE + ", " + PREFIX_ALIAS_REMOVE + ", "
            + PREFIX_INTRODUCER_REMOVE + " or " + PREFIX_NOTE_REMOVE
            + " to remove the phone, email, alias, introducer or note.\n"
            + PREFIX_NOTE + "NOTE must come last: it takes the rest of the line.\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_CONTEXT_ADD + "NUS Hackers "
            + PREFIX_CONTEXT_REMOVE + "CS2040S";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited person: %1$s";
    public static final String MESSAGE_NO_CHANGES = "No changes made to %1$s.";
    public static final String MESSAGE_ARCHIVED_NOT_EDITABLE =
            "This person is archived and can't be edited. Unarchive the person first.";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";

    public static final String MESSAGE_CONTEXTS_NOT_FOUND_SKIPPED = "Note: %1$s does not have context(s): %2$s. "
            + "Check the spelling. Skipped.";
    public static final String MESSAGE_NO_CONTEXT_LEFT = "A person must have at least one context. "
            + "Add a new context with +c/ before removing the last one.";
    public static final String MESSAGE_CONTEXTS_SKIPPED = "Note: %1$s already has context(s): %2$s. Skipped.";
    public static final String MESSAGE_NO_LONGER_MATCHES =
            "Note: %1$s no longer matches the current search. Run list to see all persons.";

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
        Person personToEdit = getPersonToEdit(model);
        Person editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);

        Optional<Person> existingPerson = model.findDuplicate(editedPerson, personToEdit);
        if (existingPerson.isPresent()) {
            throw new CommandException(Messages.getErrorMessageForDuplicatePerson(existingPerson.get(), editedPerson));
        }

        boolean isChanged = !hasSameDetails(personToEdit, editedPerson);
        if (isChanged) {
            model.setPerson(personToEdit, editedPerson);
        }

        return new CommandResult(buildFeedback(model, personToEdit, editedPerson, isChanged));
    }

    /**
     * Returns the person at {@code index} in the filtered person list of {@code model}.
     *
     * @throws CommandException if the index is out of range or the person is archived.
     */
    private Person getPersonToEdit(Model model) throws CommandException {
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());

        if (personToEdit.isArchived()) {
            throw new CommandException(MESSAGE_ARCHIVED_NOT_EDITABLE);
        }
        return personToEdit;
    }

    /**
     * Returns the message to show after the edit: the success or no-change message,
     * then a note for each of the unmatched and skipped contexts, if any, and a note if the edited person
     * no longer matches the search whose results {@code model} shows.
     */
    private String buildFeedback(Model model, Person personToEdit, Person editedPerson, boolean isChanged) {
        String feedback = isChanged
                ? String.format(MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson))
                : String.format(MESSAGE_NO_CHANGES, personToEdit.getName());
        List<Context> unmatchedContexts = findUnmatchedContexts(personToEdit, editPersonDescriptor);
        if (!unmatchedContexts.isEmpty()) {
            feedback += "\n" + String.format(MESSAGE_CONTEXTS_NOT_FOUND_SKIPPED,
                    editedPerson.getName(), Messages.formatContexts(unmatchedContexts));
        }
        List<Context> skippedContexts = findSkippedContexts(personToEdit, editPersonDescriptor);
        if (!skippedContexts.isEmpty()) {
            feedback += "\n" + String.format(MESSAGE_CONTEXTS_SKIPPED,
                    editedPerson.getName(), Messages.formatContexts(skippedContexts));
        }
        // an unchanged person was not replaced, so only a changed person can have dropped out of the list
        if (isChanged && !isShown(model, editedPerson)) {
            feedback += "\n" + String.format(MESSAGE_NO_LONGER_MATCHES, editedPerson.getName());
        }
        return feedback;
    }

    /**
     * Returns true if {@code person} is in the filtered person list of {@code model}. The very same object is
     * looked for, because two persons without a phone number or email can be equal.
     */
    private static boolean isShown(Model model, Person person) {
        return model.getFilteredPersonList().stream().anyMatch(shownPerson -> shownPerson == person);
    }

    /**
     * Returns true if {@code editedPerson} has the same details as {@code personToEdit}, spelt the same way.
     * Builds on {@code Person#equals} so a field added to {@code Person} later is compared too.
     */
    private static boolean hasSameDetails(Person personToEdit, Person editedPerson) {
        assert personToEdit != null && editedPerson != null;

        // Person#equals compares every field, but ignores case in emails and contexts,
        // e.g. alice@example.com equals Alice@Example.com
        // The two checks after it compare emails and contexts with their exact spelling,
        // so a capitalisation fix counts as a change
        return personToEdit.equals(editedPerson)
                && getEmailValue(personToEdit).equals(getEmailValue(editedPerson))
                && getContextDescriptions(personToEdit).equals(getContextDescriptions(editedPerson));
    }

    private static Optional<String> getEmailValue(Person person) {
        return person.getEmail().map(email -> email.value);
    }

    private static Set<String> getContextDescriptions(Person person) {
        return person.getContexts().stream()
                .map(context -> context.description)
                .collect(Collectors.toSet());
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Person createEditedPerson(Person personToEdit, EditPersonDescriptor editPersonDescriptor)
            throws CommandException {
        assert personToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        // Person takes null for an optional field with no value
        Phone updatedPhone = editPersonDescriptor.getPhone().applyTo(personToEdit.getPhone()).orElse(null);
        Email updatedEmail = editPersonDescriptor.getEmail().applyTo(personToEdit.getEmail()).orElse(null);
        Set<Context> updatedContexts = applyContextChanges(personToEdit, editPersonDescriptor);
        Alias updatedAlias = editPersonDescriptor.getAlias().applyTo(personToEdit.getAlias()).orElse(null);
        Introducer updatedIntroducer = editPersonDescriptor.getIntroducer()
                .applyTo(personToEdit.getIntroducer()).orElse(null);
        Note updatedNote = editPersonDescriptor.getNote().applyTo(personToEdit.getNote()).orElse(null);

        return new Person(updatedName, updatedPhone, updatedEmail, updatedContexts,
                updatedAlias, updatedIntroducer, updatedNote)
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
     * Stores the details to edit the person with. A non-null name replaces the person's name.
     * Each of the phone, email, alias, introducer and note is a {@code FieldEdit}, which leaves the
     * person's field unchanged, sets it to a new value, or removes it. Contexts are added and removed
     * by {@code contextsToAdd} and {@code contextsToRemove}.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private FieldEdit<Phone> phone = FieldEdit.unchanged();
        private FieldEdit<Email> email = FieldEdit.unchanged();
        private FieldEdit<Alias> alias = FieldEdit.unchanged();
        private FieldEdit<Introducer> introducer = FieldEdit.unchanged();
        private FieldEdit<Note> note = FieldEdit.unchanged();
        private Set<Context> contextsToAdd;
        private Set<Context> contextsToRemove;

        public EditPersonDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code contexts} is used internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            // a FieldEdit is immutable, so it can be shared
            phone = toCopy.phone;
            email = toCopy.email;
            alias = toCopy.alias;
            introducer = toCopy.introducer;
            note = toCopy.note;
            setContextsToAdd(toCopy.contextsToAdd);
            setContextsToRemove(toCopy.contextsToRemove);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, contextsToAdd, contextsToRemove)
                    || Stream.of(phone, email, alias, introducer, note).anyMatch(FieldEdit::isEdited);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(FieldEdit<Phone> phone) {
            requireNonNull(phone);
            this.phone = phone;
        }

        public FieldEdit<Phone> getPhone() {
            return phone;
        }

        public void setEmail(FieldEdit<Email> email) {
            requireNonNull(email);
            this.email = email;
        }

        public FieldEdit<Email> getEmail() {
            return email;
        }

        public void setAlias(FieldEdit<Alias> alias) {
            requireNonNull(alias);
            this.alias = alias;
        }

        public FieldEdit<Alias> getAlias() {
            return alias;
        }

        public void setIntroducer(FieldEdit<Introducer> introducer) {
            requireNonNull(introducer);
            this.introducer = introducer;
        }

        public FieldEdit<Introducer> getIntroducer() {
            return introducer;
        }

        public void setNote(FieldEdit<Note> note) {
            requireNonNull(note);
            this.note = note;
        }

        public FieldEdit<Note> getNote() {
            return note;
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
                    && Objects.equals(contextsToRemove, otherEditPersonDescriptor.contextsToRemove);
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
                    .toString();
        }
    }
}
