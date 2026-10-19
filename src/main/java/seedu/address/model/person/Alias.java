package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents an optional alternative name for a person.
 * Guarantees: immutable; valid under the same character and length rules as a {@link Name}.
 */
public class Alias {

    public static final String MESSAGE_CONSTRAINTS =
            "Aliases should start with a letter, contain only letters, spaces, hyphens (-), "
            + "apostrophes (') and periods (.), and be at most 100 characters long.";

    public final String value;

    /**
     * Constructs an {@code Alias}, removing surrounding spaces and collapsing repeated internal spaces.
     *
     * @param alias A valid alternative name.
     */
    public Alias(String alias) {
        requireNonNull(alias);
        checkArgument(isValidAlias(alias), MESSAGE_CONSTRAINTS);
        value = normalise(alias);
    }

    /**
     * Returns whether the normalised alias follows the name constraints.
     */
    public static boolean isValidAlias(String test) {
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
        return other instanceof Alias otherAlias && value.equals(otherAlias.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
