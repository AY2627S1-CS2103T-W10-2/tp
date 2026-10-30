package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Stores the plain name of the person who introduced them, without linking to another person.
 * Guarantees: immutable; valid under the same character and length rules as a {@link Name}.
 */
public class Introducer {

    public static final String MESSAGE_CONSTRAINTS =
            "Introducer names should start with a letter, contain only letters, spaces, hyphens (-), "
            + "apostrophes (') and periods (.), and be at most 100 characters long.";

    public final String value;

    /**
     * Constructs an {@code Introducer}, removing surrounding spaces and collapsing repeated internal spaces.
     *
     * @param introducer A valid person's name.
     */
    public Introducer(String introducer) {
        requireNonNull(introducer);
        checkArgument(isValidIntroducer(introducer), MESSAGE_CONSTRAINTS);
        value = normalise(introducer);
    }

    /**
     * Returns whether the normalised introducer name follows the name constraints.
     */
    public static boolean isValidIntroducer(String test) {
        return test != null && Name.isValidName(normalise(test));
    }

    private static String normalise(String value) {
        return value.trim().replaceAll(" +", " ");
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
