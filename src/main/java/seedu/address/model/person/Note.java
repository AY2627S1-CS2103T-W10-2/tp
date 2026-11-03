package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents an optional single-line personal note about a person.
 * Guarantees: immutable; non-blank after trimming and no longer than 300 characters.
 */
public class Note {

    public static final String MESSAGE_CONSTRAINTS =
            "Notes must be non-empty, contain no line breaks or control characters, "
            + "and be at most 300 characters long.";

    public final String value;

    /**
     * Constructs a {@code Note} with surrounding spaces removed.
     *
     * @param note A valid single-line note.
     */
    public Note(String note) {
        requireNonNull(note);
        checkArgument(isValidNote(note), MESSAGE_CONSTRAINTS);
        value = note.trim();
    }

    /**
     * Returns whether the trimmed note is non-blank, within the length limit, and contains no control characters.
     */
    public static boolean isValidNote(String test) {
        if (test == null) {
            return false;
        }
        String trimmed = test.trim();
        return !trimmed.isBlank() && trimmed.length() <= 300
                && test.chars().noneMatch(Note::isControlOrLineBreak);
    }

    /** Returns true for control characters and the Unicode line and paragraph separators (U+2028, U+2029). */
    private static boolean isControlOrLineBreak(int codePoint) {
        int type = Character.getType(codePoint);
        return Character.isISOControl(codePoint)
                || type == Character.LINE_SEPARATOR
                || type == Character.PARAGRAPH_SEPARATOR;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        return other instanceof Note otherNote && value.equals(otherNote.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
