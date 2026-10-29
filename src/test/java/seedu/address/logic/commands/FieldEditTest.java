package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;

public class FieldEditTest {

    private static final String VALUE = "value";
    private static final String OTHER_VALUE = "other value";
    private static final Optional<String> CURRENT_VALUE = Optional.of("current value");

    @Test
    public void set_nullValue_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> FieldEdit.set(null));
    }

    @Test
    public void isEdited() {
        assertFalse(FieldEdit.unchanged().isEdited());
        assertTrue(FieldEdit.set(VALUE).isEdited());
        assertTrue(FieldEdit.removed().isEdited());
    }

    @Test
    public void isRemoved() {
        assertFalse(FieldEdit.unchanged().isRemoved());
        assertFalse(FieldEdit.set(VALUE).isRemoved());
        assertTrue(FieldEdit.removed().isRemoved());
    }

    @Test
    public void getValue() {
        assertEquals(Optional.empty(), FieldEdit.unchanged().getValue());
        assertEquals(Optional.of(VALUE), FieldEdit.set(VALUE).getValue());
        assertEquals(Optional.empty(), FieldEdit.removed().getValue());
    }

    @Test
    public void applyTo_unchanged_returnsCurrentValue() {
        FieldEdit<String> fieldEdit = FieldEdit.unchanged();

        // field has a value -> keeps it
        assertEquals(CURRENT_VALUE, fieldEdit.applyTo(CURRENT_VALUE));

        // field has no value -> stays empty
        assertEquals(Optional.empty(), fieldEdit.applyTo(Optional.empty()));
    }

    @Test
    public void applyTo_set_returnsNewValue() {
        FieldEdit<String> fieldEdit = FieldEdit.set(VALUE);

        // field has a value -> replaces it
        assertEquals(Optional.of(VALUE), fieldEdit.applyTo(CURRENT_VALUE));

        // field has no value -> sets it
        assertEquals(Optional.of(VALUE), fieldEdit.applyTo(Optional.empty()));
    }

    @Test
    public void applyTo_removed_returnsEmpty() {
        FieldEdit<String> fieldEdit = FieldEdit.removed();

        // field has a value -> removes it
        assertEquals(Optional.empty(), fieldEdit.applyTo(CURRENT_VALUE));

        // field has no value -> stays empty
        assertEquals(Optional.empty(), fieldEdit.applyTo(Optional.empty()));
    }

    @Test
    public void equals() {
        FieldEdit<String> setEdit = FieldEdit.set(VALUE);

        // same values -> returns true
        assertTrue(setEdit.equals(FieldEdit.set(VALUE)));
        assertTrue(FieldEdit.<String>unchanged().equals(FieldEdit.<String>unchanged()));
        assertTrue(FieldEdit.<String>removed().equals(FieldEdit.<String>removed()));

        // same object -> returns true
        assertTrue(setEdit.equals(setEdit));

        // null -> returns false
        assertFalse(setEdit.equals(null));

        // different types -> returns false
        assertFalse(setEdit.equals(5));

        // different value -> returns false
        assertFalse(setEdit.equals(FieldEdit.set(OTHER_VALUE)));

        // different state -> returns false
        assertFalse(setEdit.equals(FieldEdit.<String>unchanged()));
        assertFalse(setEdit.equals(FieldEdit.<String>removed()));
        assertFalse(FieldEdit.<String>unchanged().equals(FieldEdit.<String>removed()));
    }

    @Test
    public void hashCode_equalFieldEdits_returnsSameHashCode() {
        assertEquals(FieldEdit.set(VALUE).hashCode(), FieldEdit.set(VALUE).hashCode());
        assertEquals(FieldEdit.unchanged().hashCode(), FieldEdit.unchanged().hashCode());
        assertEquals(FieldEdit.removed().hashCode(), FieldEdit.removed().hashCode());
    }

    @Test
    public void toStringMethod() {
        String className = FieldEdit.class.getCanonicalName();

        assertEquals(className + "{state=UNCHANGED, value=null}", FieldEdit.unchanged().toString());
        assertEquals(className + "{state=SET, value=" + VALUE + "}", FieldEdit.set(VALUE).toString());
        assertEquals(className + "{state=REMOVED, value=null}", FieldEdit.removed().toString());
    }
}
