package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import seedu.address.commons.util.StringUtil;

/**
 * Stores the plain name of the person who introduced a contact, without linking to another contact.
 * Guarantees: immutable; valid under the same character and length rules as a {@link Name}.
 */
public class Introducer {

    public static final String MESSAGE_CONSTRAINTS =
            "Introducer names should start with a letter, contain only letters, spaces, hyphens (-), "
            + "apostrophes (') and periods (.), and be at most 100 characters long.";

    public final String value;

    /**
     * Constructs an {@code Introducer}, removing surrounding whitespace and collapsing repeated internal whitespace.
     *
     * @param introducer A valid person's name.
     */
    public Introducer(String introducer) {
        requireNonNull(introducer);
        checkArgument(isValidIntroducer(introducer), MESSAGE_CONSTRAINTS);
        value = StringUtil.collapseWhitespace(introducer);
    }

    /**
     * Returns whether the normalised introducer name follows the name constraints.
     */
    public static boolean isValidIntroducer(String test) {
        return test != null && Name.isValidName(StringUtil.collapseWhitespace(test));
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
        return other instanceof Introducer otherIntroducer && value.equals(otherIntroducer.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
