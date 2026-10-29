package seedu.address.logic.parser;

import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Represents a function that parses the value of a field, such as {@link ParserUtil#parsePhone}.
 */
@FunctionalInterface
public interface FieldParser<T> {
    T parse(String value) throws ParseException;
}
