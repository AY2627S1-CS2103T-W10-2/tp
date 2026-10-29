package seedu.address.logic.parser;

import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Represents a function that parses the value of a field, such as {@link ParserUtil#parsePhone}.
 */
@FunctionalInterface
public interface FieldParser<T> {
    /**
     * Parses {@code value} into the value of a field.
     *
     * @throws ParseException if {@code value} is invalid.
     */
    T parse(String value) throws ParseException;
}
