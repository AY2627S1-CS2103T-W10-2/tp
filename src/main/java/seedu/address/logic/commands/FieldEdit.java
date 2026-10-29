package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;

/**
 * An edit to an optional field of a person. The edit leaves the field unchanged, sets it to a new value,
 * or removes it. A field cannot be both set and removed.
 *
 * @param <T> the type of the field, such as {@code Phone}.
 */
public final class FieldEdit<T> {

    private enum State { UNCHANGED, SET, REMOVED }

    private final State state;
    // non-null only if state is SET
    private final T value;

    private FieldEdit(State state, T value) {
        this.state = state;
        this.value = value;
    }

    /**
     * Returns an edit that leaves the field unchanged.
     */
    public static <T> FieldEdit<T> unchanged() {
        return new FieldEdit<>(State.UNCHANGED, null);
    }

    /**
     * Returns an edit that sets the field to {@code value}.
     */
    public static <T> FieldEdit<T> set(T value) {
        requireNonNull(value);
        return new FieldEdit<>(State.SET, value);
    }

    /**
     * Returns an edit that removes the field.
     */
    public static <T> FieldEdit<T> removed() {
        return new FieldEdit<>(State.REMOVED, null);
    }

    /**
     * Returns true if this edit sets or removes the field.
     */
    public boolean isEdited() {
        return state != State.UNCHANGED;
    }

    /**
     * Returns true if this edit removes the field.
     */
    public boolean isRemoved() {
        return state == State.REMOVED;
    }

    /**
     * Returns the new value of the field, or {@code Optional#empty()} if this edit does not set the field.
     */
    public Optional<T> getValue() {
        return Optional.ofNullable(value);
    }

    /**
     * Returns the value of the field after this edit, given its {@code currentValue}.
     * The result is empty if the field is removed or {@code currentValue} is empty and the field is unchanged.
     */
    public Optional<T> applyTo(Optional<T> currentValue) {
        return switch (state) {
            case REMOVED -> Optional.empty();
            case SET -> Optional.of(value);
            case UNCHANGED -> currentValue;
        };
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FieldEdit<?> otherFieldEdit)) {
            return false;
        }

        return state == otherFieldEdit.state
                && Objects.equals(value, otherFieldEdit.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, value);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("state", state)
                .add("value", value)
                .toString();
    }
}
