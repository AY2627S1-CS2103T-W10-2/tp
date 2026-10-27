package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ALIAS_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ALIAS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CONTEXT_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CONTEXT_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_INTRODUCER_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_INTRODUCER_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NOTE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NOTE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.testutil.EditPersonDescriptorBuilder;

public class EditPersonDescriptorTest {

    @Test
    public void equals() {
        // same values -> returns true
        EditPersonDescriptor descriptorWithSameValues = new EditPersonDescriptor(DESC_AMY);
        assertTrue(DESC_AMY.equals(descriptorWithSameValues));

        // same object -> returns true
        assertTrue(DESC_AMY.equals(DESC_AMY));

        // null -> returns false
        assertFalse(DESC_AMY.equals(null));

        // different types -> returns false
        assertFalse(DESC_AMY.equals(5));

        // different values -> returns false
        assertFalse(DESC_AMY.equals(DESC_BOB));

        // different name -> returns false
        EditPersonDescriptor editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withName(VALID_NAME_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different phone -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withPhone(VALID_PHONE_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different email -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different contexts -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withContextsToAdd(VALID_CONTEXT_HUSBAND).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different contexts to remove -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withContextsToRemove(VALID_CONTEXT_FRIEND).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different alias -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withAlias(VALID_ALIAS_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different introducer -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withIntroducer(VALID_INTRODUCER_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different note -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withNote(VALID_NOTE_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));
    }

    @Test
    public void equals_differentFieldRemoved_returnsFalse() {
        EditPersonDescriptor descriptor = new EditPersonDescriptor();

        assertFalse(descriptor.equals(new EditPersonDescriptorBuilder().withPhoneRemoved().build()));
        assertFalse(descriptor.equals(new EditPersonDescriptorBuilder().withEmailRemoved().build()));
        assertFalse(descriptor.equals(new EditPersonDescriptorBuilder().withAliasRemoved().build()));
        assertFalse(descriptor.equals(new EditPersonDescriptorBuilder().withIntroducerRemoved().build()));
        assertFalse(descriptor.equals(new EditPersonDescriptorBuilder().withNoteRemoved().build()));
    }

    @Test
    public void isAnyFieldEdited() {
        assertFalse(new EditPersonDescriptor().isAnyFieldEdited());

        // setting or removing a single alias, introducer or note counts as an edit
        assertTrue(new EditPersonDescriptorBuilder().withAlias(VALID_ALIAS_AMY).build().isAnyFieldEdited());
        assertTrue(new EditPersonDescriptorBuilder().withIntroducer(VALID_INTRODUCER_AMY).build()
                .isAnyFieldEdited());
        assertTrue(new EditPersonDescriptorBuilder().withNote(VALID_NOTE_AMY).build().isAnyFieldEdited());
        assertTrue(new EditPersonDescriptorBuilder().withAliasRemoved().build().isAnyFieldEdited());
        assertTrue(new EditPersonDescriptorBuilder().withIntroducerRemoved().build().isAnyFieldEdited());
        assertTrue(new EditPersonDescriptorBuilder().withNoteRemoved().build().isAnyFieldEdited());
    }

    @Test
    public void toStringMethod() {
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        String expected = EditPersonDescriptor.class.getCanonicalName() + "{name="
                + editPersonDescriptor.getName().orElse(null) + ", phone="
                + editPersonDescriptor.getPhone().orElse(null) + ", email="
                + editPersonDescriptor.getEmail().orElse(null) + ", alias="
                + editPersonDescriptor.getAlias().orElse(null) + ", introducer="
                + editPersonDescriptor.getIntroducer().orElse(null) + ", note="
                + editPersonDescriptor.getNote().orElse(null) + ", contextsToAdd="
                + editPersonDescriptor.getContextsToAdd().orElse(null) + ", contextsToRemove="
                + editPersonDescriptor.getContextsToRemove().orElse(null) + ", isPhoneRemoved="
                + editPersonDescriptor.isPhoneRemoved() + ", isEmailRemoved="
                + editPersonDescriptor.isEmailRemoved() + ", isAliasRemoved="
                + editPersonDescriptor.isAliasRemoved() + ", isIntroducerRemoved="
                + editPersonDescriptor.isIntroducerRemoved() + ", isNoteRemoved="
                + editPersonDescriptor.isNoteRemoved() + "}";
        assertEquals(expected, editPersonDescriptor.toString());
    }
}
