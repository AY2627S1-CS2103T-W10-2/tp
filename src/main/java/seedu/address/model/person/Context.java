package seedu.address.model.person;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;

/**
 * Represents a context (e.g. a module, club or event) that a {@code Contact} is linked to.
 * Guarantees: immutable; description is valid as declared in {@link #isValidDescription(String)}.
 * Two contexts are equal if their descriptions match, ignoring capitalisation and surrounding spaces.
 */
public class Context {

    public static final String MESSAGE_CONSTRAINTS = "Context descriptions should not be blank";

    public final String description;

    /**
     * Constructs a {@code Context}.
     *
     * @param description A valid description.
     */
    public Context(String description) {
        checkArgument(isValidDescription(description), MESSAGE_CONSTRAINTS);
        this.description = description.trim();
    }

    /**
     * Returns true if a given string is a valid context description.
     */
    public static boolean isValidDescription(String test) {
        return test != null && !test.isBlank();
    }

    @Override
    public String toString() {
        return description;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Context otherContext)) {
            return false;
        }

        return description.equalsIgnoreCase(otherContext.description);
    }

    @Override
    public int hashCode() {
        return description.toLowerCase(Locale.ROOT).hashCode();
    }
}
