package seedu.address.testutil;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.person.Alias;
import seedu.address.model.person.Context;
import seedu.address.model.person.Email;
import seedu.address.model.person.Introducer;
import seedu.address.model.person.Name;
import seedu.address.model.person.Note;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

/**
 * A utility class to help with building EditPersonDescriptor objects.
 */
public class EditPersonDescriptorBuilder {

    private EditPersonDescriptor descriptor;

    public EditPersonDescriptorBuilder() {
        descriptor = new EditPersonDescriptor();
    }

    public EditPersonDescriptorBuilder(EditPersonDescriptor descriptor) {
        this.descriptor = new EditPersonDescriptor(descriptor);
    }

    /**
     * Returns an {@code EditPersonDescriptor} with fields containing {@code person}'s details
     */
    public EditPersonDescriptorBuilder(Person person) {
        descriptor = new EditPersonDescriptor();
        descriptor.setName(person.getName());
        person.getPhone().ifPresent(descriptor::setPhone);
        person.getEmail().ifPresent(descriptor::setEmail);
        person.getAlias().ifPresent(descriptor::setAlias);
        person.getIntroducer().ifPresent(descriptor::setIntroducer);
        person.getNote().ifPresent(descriptor::setNote);
        descriptor.setContextsToAdd(person.getContexts());
    }

    /**
     * Sets the {@code Name} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withName(String name) {
        descriptor.setName(new Name(name));
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withPhone(String phone) {
        descriptor.setPhone(new Phone(phone));
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withEmail(String email) {
        descriptor.setEmail(new Email(email));
        return this;
    }

    /**
     * Sets the {@code Alias} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withAlias(String alias) {
        descriptor.setAlias(new Alias(alias));
        return this;
    }

    /**
     * Sets the {@code Introducer} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withIntroducer(String introducer) {
        descriptor.setIntroducer(new Introducer(introducer));
        return this;
    }

    /**
     * Sets the {@code Note} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withNote(String note) {
        descriptor.setNote(new Note(note));
        return this;
    }

    /**
     * Marks the phone for removal in the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withPhoneRemoved() {
        descriptor.setPhoneRemoved(true);
        return this;
    }

    /**
     * Marks the email for removal in the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withEmailRemoved() {
        descriptor.setEmailRemoved(true);
        return this;
    }

    /**
     * Marks the alias for removal in the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withAliasRemoved() {
        descriptor.setAliasRemoved(true);
        return this;
    }

    /**
     * Marks the introducer for removal in the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withIntroducerRemoved() {
        descriptor.setIntroducerRemoved(true);
        return this;
    }

    /**
     * Marks the note for removal in the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withNoteRemoved() {
        descriptor.setNoteRemoved(true);
        return this;
    }

    /**
     * Parses the {@code contexts} into a {@code Set<Context>} and sets it to the {@code EditPersonDescriptor}
     * that we are building.
     */
    public EditPersonDescriptorBuilder withContextsToAdd(String... contexts) {
        Set<Context> contextSet = Stream.of(contexts).map(Context::new)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        descriptor.setContextsToAdd(contextSet);
        return this;
    }

    /**
     * Parses the {@code contexts} into a {@code Set<Context>} and sets them as the contexts to remove
     * in the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withContextsToRemove(String... contexts) {
        Set<Context> contextSet = Stream.of(contexts).map(Context::new)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        descriptor.setContextsToRemove(contextSet);
        return this;
    }

    public EditPersonDescriptor build() {
        return descriptor;
    }
}
