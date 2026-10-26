package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class NoteTest {

    @Test
    public void constructor_trimsSurroundingSpaces() {
        assertEquals("Working on UI", new Note("  Working on UI  ").value);
    }

    @Test
    public void constructor_invalidNote_throwsIllegalArgumentException() {
        assertThrows(NullPointerException.class, () -> new Note(null));
        assertThrows(IllegalArgumentException.class, () -> new Note("  "));
        assertThrows(IllegalArgumentException.class, () -> new Note("Working\non UI"));
        assertThrows(IllegalArgumentException.class, () -> new Note("A".repeat(301)));
    }

    @Test
    public void isValidNote() {
        assertFalse(Note.isValidNote(null));
        assertFalse(Note.isValidNote("Note\t"));
        assertFalse(Note.isValidNote("line one\u2028line two")); // Unicode line separator
        assertFalse(Note.isValidNote("para one\u2029para two")); // Unicode paragraph separator
        assertFalse(Note.isValidNote("next\u0085line")); // next-line control character
        assertTrue(Note.isValidNote("  Project UI, v1.4!  "));
        assertTrue(Note.isValidNote("A".repeat(300)));
    }

    @Test
    public void equals_trimmedValues() {
        Note note = new Note("Working on UI");
        Note sameNote = new Note(" Working on UI ");
        assertEquals(note, sameNote);
        assertEquals(note.hashCode(), sameNote.hashCode());
        assertFalse(note.equals(new Note("Working on tests")));
    }
}
